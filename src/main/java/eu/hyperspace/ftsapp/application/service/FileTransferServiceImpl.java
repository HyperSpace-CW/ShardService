package eu.hyperspace.ftsapp.application.service;

import eu.hyperspace.ftsapp.application.domain.dto.file.FileBase64DTO;
import eu.hyperspace.ftsapp.application.domain.dto.file.FileFullDataDTO;
import eu.hyperspace.ftsapp.application.domain.dto.file.FileNameDTO;
import eu.hyperspace.ftsapp.application.domain.exception.FailedToCreateBucketException;
import eu.hyperspace.ftsapp.application.domain.exception.FailedToDeleteFileException;
import eu.hyperspace.ftsapp.application.domain.exception.FailedToDownloadFileException;
import eu.hyperspace.ftsapp.application.domain.exception.FailedToUpdateFileException;
import eu.hyperspace.ftsapp.application.domain.exception.FailedToUploadFileException;
import eu.hyperspace.ftsapp.application.domain.exception.FileAlreadyExistsException;
import eu.hyperspace.ftsapp.application.domain.exception.FileNotFoundException;
import eu.hyperspace.ftsapp.application.port.in.FileTransferService;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.errors.ErrorResponseException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class FileTransferServiceImpl implements FileTransferService {

    private final MinioClient minioClient;

    @Value("${minio.bucketName}")
    private String bucketName;

    @Override
    public FileFullDataDTO uploadFile(FileFullDataDTO fileFullDataDTO) {
        try {
            createBucketIfNotExists();

            byte[] fileBytes = Base64.getDecoder().decode(fileFullDataDTO.getBase64File());
            ByteArrayInputStream inputStream = new ByteArrayInputStream(fileBytes);

            if (fileExists(fileFullDataDTO.getFileName()))
                throw new FileAlreadyExistsException("file " + fileFullDataDTO.getFileName() + " already exists");

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileFullDataDTO.getFileName())
                            .stream(inputStream, fileBytes.length, -1)
                            .build()
            );
            return fileFullDataDTO;
        } catch (Exception e) {
            throw new FailedToUploadFileException("Failed to upload file: " + e.getMessage());
        }
    }

    @Override
    public FileFullDataDTO updateFile(FileFullDataDTO fileFullDataDTO) {
        try {
            if (!fileExists(fileFullDataDTO.getFileName()))
                throw new FileNotFoundException("File " + fileFullDataDTO.getFileName() + " does not exist");

            byte[] fileBytes = Base64.getDecoder().decode(fileFullDataDTO.getBase64File());
            ByteArrayInputStream inputStream = new ByteArrayInputStream(fileBytes);

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileFullDataDTO.getFileName())
                            .stream(inputStream, fileBytes.length, -1)
                            .build()
            );
            return fileFullDataDTO;
        } catch (Exception e) {
            throw new FailedToUpdateFileException("Failed to update file: " + e.getMessage());
        }
    }

    @Override
    public FileBase64DTO downloadFile(String fileName) {
        try {
            if (!fileExists(fileName)) {
                throw new FileNotFoundException("file " + fileName + " not found");
            }

            try (InputStream inputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            )) {
                byte[] fileBytes = inputStream.readAllBytes();
                return new FileBase64DTO(Base64.getEncoder().encodeToString(fileBytes));
            }
        } catch (Exception e) {
            throw new FailedToDownloadFileException("Failed to download file: " + e.getMessage());
        }
    }

    @Override
    public FileNameDTO deleteFile(String fileName) {
        try {
            if (!fileExists(fileName))
                throw new FileNotFoundException("file " + fileName + " does not exist");

            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );
            return new FileNameDTO(fileName);
        } catch (Exception e) {
            throw new FailedToDeleteFileException("Failed to delete file: " + e.getMessage());
        }
    }

    private void createBucketIfNotExists() {
        try {
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
        } catch (Exception e) {
            throw new FailedToCreateBucketException("Failed to create bucket: " + e.getMessage());
        }
    }

    private boolean fileExists(String fileName) throws Exception {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );
            return true;
        } catch (ErrorResponseException e) {
            if (!e.errorResponse().code().equals("NoSuchKey")) {
                throw e;
            }
            return false;
        }
    }
}
