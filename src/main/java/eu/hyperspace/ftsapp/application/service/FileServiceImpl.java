package eu.hyperspace.ftsapp.application.service;

import eu.hyperspace.ftsapp.application.domain.dto.file.FileInfoDTO;
import eu.hyperspace.ftsapp.application.domain.dto.file.FileNameDTO;
import eu.hyperspace.ftsapp.application.domain.entity.SFile;
import eu.hyperspace.ftsapp.application.domain.entity.Shard;
import eu.hyperspace.ftsapp.application.domain.enums.AccessLevel;
import eu.hyperspace.ftsapp.application.domain.exception.AccessDeniedException;
import eu.hyperspace.ftsapp.application.domain.exception.FailedToDownloadFileException;
import eu.hyperspace.ftsapp.application.domain.exception.FileAlreadyExistsException;
import eu.hyperspace.ftsapp.application.domain.exception.FileNotFoundException;
import eu.hyperspace.ftsapp.application.domain.exception.FileSizeLimitExceededException;
import eu.hyperspace.ftsapp.application.domain.exception.ParamNotValidException;
import eu.hyperspace.ftsapp.application.port.in.FileService;
import eu.hyperspace.ftsapp.application.port.in.FileTransferService;
import eu.hyperspace.ftsapp.application.port.in.ShardService;
import eu.hyperspace.ftsapp.application.port.out.FileRepository;
import eu.hyperspace.ftsapp.application.util.mapper.FileMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;

    @Lazy
    private final ShardService shardService;

    private final FileTransferService fileTransferService;

    private final FileMapper fileMapper;

    @Value("${service-limits.shard-capacity}")
    private Long shardCapacity;

    @Value("${service-limits.single-upload-capacity}")
    private Long singleUploadCapacity;


    @Override
    @Transactional
    public List<FileInfoDTO> getShardFiles(Long shardId) {
        if (!shardService.shardExistsById(shardId)) {
            throw new EntityNotFoundException("Shard with id " + shardId + " not found.");
        }
        Long currentUserId = shardService.getCurrentUserId();

        if (!shardService.hasAccess(shardId, currentUserId, AccessLevel.OWNER, AccessLevel.WRITE, AccessLevel.READ)) {
            throw new AccessDeniedException();
        }

        return fileRepository
                .findAllByShardId(shardId)
                .stream()
                .map(fileMapper::toDto).toList();
    }

    @Override
    @Transactional
    public List<FileInfoDTO> uploadFiles(List<MultipartFile> files, Long shardId) {
        Shard shard = shardService.getShardEntityById(shardId);
        Long currentUserId = shardService.getCurrentUserId();

        if (!shardService.hasAccess(shardId, currentUserId, AccessLevel.OWNER, AccessLevel.WRITE)) {
            throw new AccessDeniedException();
        }

        validateFileNames(files, shardId);

        validateFilesSize(files, shard);

        List<SFile> sFiles = files.stream()
                .map(file -> {
                    SFile sFile = fileMapper.toEntity(file);
                    sFile.setShard(shard);
                    fileTransferService.uploadFile(file, sFile.getMinioName());
                    return sFile;
                })
                .toList();
        List<SFile> savedFiles = fileRepository.saveAll(sFiles);

        long totalFilesSize = files.stream().mapToLong(MultipartFile::getSize).sum();
        shardService.updateShardSize(shardId, totalFilesSize);
        shardService.updateShardFilesCount(shardId, (long) savedFiles.size());

        return savedFiles.stream()
                .map(fileMapper::toDto)
                .toList();
    }

    private void validateFileNames(List<MultipartFile> files, Long shardId) {
        Set<String> incomingNames = files.stream()
                .map(MultipartFile::getOriginalFilename)
                .collect(Collectors.toSet());
        if (incomingNames.size() < files.size()) {
            throw new ParamNotValidException("Duplicate names in upload batch");
        }

        List<String> longFileNames = incomingNames.stream()
                .filter(name -> name.length() > 32)
                .toList();
        if (!longFileNames.isEmpty()) {
            throw new ParamNotValidException(
                    "These files has name exceeds maximum length of 32 characters: " + longFileNames
            );
        }

        List<String> duplicateNames = fileRepository.findExistingNamesInShard(shardId, incomingNames);
        if (!duplicateNames.isEmpty()) {
            throw new FileAlreadyExistsException(
                    "Files with these names already exist in shard: " + duplicateNames);
        }
    }

    private void validateFilesSize(List<MultipartFile> files, Shard shard) {
        long filesSize = files.stream()
                .mapToLong(MultipartFile::getSize)
                .sum();

        if (filesSize > singleUploadCapacity) {
            throw new FileSizeLimitExceededException(
                    "Total files size " + filesSize / (1024 * 1024) +
                            " MB exceeds single upload max limit " + singleUploadCapacity / (1024 * 1024) + " MB"
            );
        }

        long totalSize = filesSize + shard.getTotalSize();

        if (totalSize > shardCapacity) {
            throw new FileSizeLimitExceededException(
                    "Total files size " + totalSize / (1024 * 1024) +
                            " MB exceeds shard max limit " + shardCapacity / (1024 * 1024) + " MB"
            );
        }
    }

    @Override
    @Transactional
    public FileInfoDTO updateFile(FileNameDTO fileNameDTO, Long id) {
        SFile file = fileRepository.findById(id).orElseThrow(
                () -> new FileNotFoundException("File with id " + id + " not found")
        );
        Long currentUserId = shardService.getCurrentUserId();
        Long shardId = file.getShard().getId();

        if (!shardService.hasAccess(shardId, currentUserId, AccessLevel.OWNER, AccessLevel.WRITE)) {
            throw new AccessDeniedException();
        }

        String oldFileName = file.getName();
        int lastDotIndex = oldFileName.lastIndexOf('.');
        String extension = (lastDotIndex != -1) ? oldFileName.substring(lastDotIndex) : "";
        String newFileName = fileNameDTO.getFileName() + extension;

        List<SFile> shardFiles = fileRepository.findAllByShardId(file.getShard().getId());
        for (SFile shardFile : shardFiles) {
            if (shardFile.getName().equals(newFileName)) {
                throw new FileAlreadyExistsException("File with this name already exists in shard");
            }
        }

        file.setName(newFileName);
        return fileMapper.toDto(fileRepository.save(file));
    }

    @Override
    @Transactional
    public ByteArrayResource downloadFiles(List<Long> fileIds) {
        Long currentUserId = shardService.getCurrentUserId();

        findAbsentFiles(fileIds);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            for (Long id : fileIds) {
                SFile file = fileRepository.findById(id)
                        .orElseThrow(() -> new FileNotFoundException("File with id " + id + " not found"));

                Long shardId = file.getShard().getId();
                if (!shardService.hasAccess(shardId, currentUserId, AccessLevel.OWNER, AccessLevel.WRITE, AccessLevel.READ)) {
                    throw new AccessDeniedException();
                }

                MultipartFile multipartFile = fileTransferService.downloadFile(file);

                ZipEntry entry = new ZipEntry(multipartFile.getOriginalFilename());
                entry.setSize(multipartFile.getSize());
                zos.putNextEntry(entry);
                zos.write(multipartFile.getBytes());
                zos.closeEntry();
            }
        } catch (IOException e) {
            throw new FailedToDownloadFileException("Failed to create zip archive: " + e.getMessage());
        }

        return new ByteArrayResource(baos.toByteArray());
    }

    private void findAbsentFiles(List<Long> fileIds) {
        List<Long> absentFiles = fileIds.stream()
                .filter(id -> !fileRepository.existsById(id))
                .toList();

        if (!absentFiles.isEmpty()) {
            throw new FileNotFoundException(
                    "Files with these ids are absent: " + absentFiles);
        }
    }

    @Override
    @Transactional
    public List<FileInfoDTO> deleteFiles(List<Long> fileIds) {
        Long currentUserId = shardService.getCurrentUserId();
        findAbsentFiles(fileIds);
        List<FileInfoDTO> deletedFiles = new ArrayList<>();
        for (Long id : fileIds) {
            SFile file = fileRepository.findById(id).orElseThrow(
                    () -> new FileNotFoundException("File with id " + id + " not found")
            );

            Long shardId = file.getShard().getId();
            if (!shardService.hasAccess(shardId, currentUserId, AccessLevel.OWNER, AccessLevel.WRITE)) {
                throw new AccessDeniedException();
            }

            Shard shard = file.getShard();
            shardService.updateShardSize(shard.getId(),  -file.getSize());
            shardService.updateShardFilesCount(shard.getId(), -1L);
            fileTransferService.deleteFile(file.getMinioName());
            fileRepository.delete(file);
            deletedFiles.add(fileMapper.toDto(file));
        }
        return deletedFiles;
    }

    @Override
    public void deleteFilesByShard(Shard shard) {
        List<SFile> files = fileRepository.findAllByShardId(shard.getId());
        files.forEach(file -> fileTransferService.deleteFile(file.getMinioName()));
        fileRepository.deleteAll(files);
    }
}
