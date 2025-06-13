package eu.hyperspace.ftsapp.application.service;

import eu.hyperspace.ftsapp.application.domain.dto.file.FileInfoDTO;
import eu.hyperspace.ftsapp.application.domain.dto.file.FileNameDTO;
import eu.hyperspace.ftsapp.application.domain.entity.SFile;
import eu.hyperspace.ftsapp.application.domain.entity.Shard;
import eu.hyperspace.ftsapp.application.domain.exception.FailedToDownloadFileException;
import eu.hyperspace.ftsapp.application.domain.exception.FileAlreadyExistsException;
import eu.hyperspace.ftsapp.application.domain.exception.FileNotFoundException;
import eu.hyperspace.ftsapp.application.domain.exception.FileSizeLimitExceededException;
import eu.hyperspace.ftsapp.application.port.in.FileService;
import eu.hyperspace.ftsapp.application.port.in.FileTransferService;
import eu.hyperspace.ftsapp.application.port.in.ShardService;
import eu.hyperspace.ftsapp.application.port.out.FileRepository;
import eu.hyperspace.ftsapp.application.util.mapper.FileMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;

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

        return fileRepository
                .findAllByShardId(shardId)
                .stream()
                .map(fileMapper::toDto).toList();
    }

    @Override
    @Transactional
    public List<FileInfoDTO> uploadFiles(List<MultipartFile> files, Long shardId) {
        if (!shardService.shardExistsById(shardId)) {
            throw new EntityNotFoundException("Shard with id " + shardId + " not found.");
        }

        validateFileNamesUniqueness(files, shardId);

        validateFilesSize(files, shardId);

        Shard shard = shardService.getShardEntityById(shardId);

        return files.stream()
                .map(file -> {
                    SFile sFile = fileMapper.toEntity(file);
                    sFile.setShard(shard);
                    fileTransferService.uploadFile(file, sFile.getMinioName());
                    return fileMapper.toDto(fileRepository.save(sFile));
                })
                .toList();
    }

    private void validateFileNamesUniqueness(List<MultipartFile> files, Long shardId) {
        List<String> duplicateNames = files.stream()
                .map(MultipartFile::getOriginalFilename)
                .filter(name -> fileRepository.existsByShardIdAndName(shardId, name))
                .toList();

        if (!duplicateNames.isEmpty()) {
            throw new FileAlreadyExistsException(
                    "Files with these names already exist in shard: " + duplicateNames);
        }
    }

    private void validateFilesSize(List<MultipartFile> files, Long shardId) {
        long filesSize = files.stream()
                .mapToLong(MultipartFile::getSize)
                .sum();

        if (filesSize > singleUploadCapacity) {
            throw new FileSizeLimitExceededException(
                    "Total files size " + filesSize / (1024 * 1024) +
                            " MB exceeds single upload max limit " + singleUploadCapacity / (1024 * 1024) + " MB"
            );
        }

        Shard shard = shardService.getShardEntityById(shardId);
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
        List<SFile> shardFiles = fileRepository.findAllByShardId(file.getShard().getId());
        for (SFile shardFile : shardFiles) {
            if (shardFile.getName().equals(fileNameDTO.getFileName()))
                throw new FileAlreadyExistsException("File with this name already exists in shard");
        }
        file.setName(fileNameDTO.getFileName());
        return fileMapper.toDto(fileRepository.save(file));
    }

    @Override
    @Transactional
    public ByteArrayResource downloadFiles(List<Long> fileIds) {
        findAbsentFiles(fileIds);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            for (Long id : fileIds) {
                SFile file = fileRepository.findById(id)
                        .orElseThrow(() -> new FileNotFoundException("File with id " + id + " not found"));
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
        findAbsentFiles(fileIds);
        List<FileInfoDTO> deletedFiles = new ArrayList<>();
        for (Long id : fileIds) {
            SFile file = fileRepository.findById(id).orElseThrow(
                    () -> new FileNotFoundException("File with id " + id + " not found")
            );
            fileTransferService.deleteFile(file.getMinioName());
            fileRepository.delete(file);
            deletedFiles.add(fileMapper.toDto(file));
        }
        return deletedFiles;
    }
}
