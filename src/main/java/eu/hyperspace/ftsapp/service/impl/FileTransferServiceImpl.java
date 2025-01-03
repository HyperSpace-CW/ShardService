package eu.hyperspace.ftsapp.service.impl;

import eu.hyperspace.ftsapp.dto.FileBase64DTO;
import eu.hyperspace.ftsapp.dto.FileNameDTO;
import eu.hyperspace.ftsapp.dto.FileFullDataDTO;
import eu.hyperspace.ftsapp.exception.FailedToCreateBucketException;
import eu.hyperspace.ftsapp.exception.FailedToDeleteFileException;
import eu.hyperspace.ftsapp.exception.FailedToUploadFileException;
import eu.hyperspace.ftsapp.exception.FileNotFoundException;
import eu.hyperspace.ftsapp.service.FileTransferService;
import io.minio.*;
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
            byte[] fileBytes = Base64.getDecoder().decode(fileFullDataDTO.getBase64File());
            ByteArrayInputStream inputStream = new ByteArrayInputStream(fileBytes);

            createBucketIfNotExists();

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileFullDataDTO.getFileName())
                            .stream(inputStream, fileBytes.length, -1)
                            .build()
            );
            return fileFullDataDTO;
        } catch (Exception e) {
            throw new FailedToUploadFileException("Failed to upload file: " + e.getMessage() + " , error: " + e.getMessage());
        }
    }

    @Override
    public FileBase64DTO downloadFile(String fileName) {
        try (InputStream inputStream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .build()
        )) {
            byte[] fileBytes = inputStream.readAllBytes();

            return new FileBase64DTO(Base64.getEncoder().encodeToString(fileBytes));
        } catch (Exception e) {
            throw new FileNotFoundException("File " + fileName + " not found, error: " + e.getMessage());
        }
    }

    @Override
    public FileNameDTO deleteFile(String fileName) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );

            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );
            return new FileNameDTO(fileName);
        } catch (ErrorResponseException e) {
            if (e.errorResponse().code().equals("NoSuchKey")) {
                throw new FileNotFoundException("File " + fileName + " does not exist");
            } else {
                throw new FailedToDeleteFileException("Failed to delete file: " + fileName + ", error: " + e.getMessage());
            }
        } catch (Exception e) {
            throw new FailedToDeleteFileException("Failed to delete file: " + fileName + ", error: " + e.getMessage());
        }
    }

    private void createBucketIfNotExists() {
        try {
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
        } catch (Exception e) {
            throw new FailedToCreateBucketException("Failed to create bucket: " + bucketName + ", error: " + e.getMessage());
        }
    }
}
