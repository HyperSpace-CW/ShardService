package eu.hyperspace.ftsapp.application.service;

import eu.hyperspace.ftsapp.application.domain.entity.SFile;
import eu.hyperspace.ftsapp.application.domain.exception.FailedToCreateBucketException;
import eu.hyperspace.ftsapp.application.domain.exception.FailedToDeleteFileException;
import eu.hyperspace.ftsapp.application.domain.exception.FailedToDownloadFileException;
import eu.hyperspace.ftsapp.application.domain.exception.FailedToUploadFileException;
import eu.hyperspace.ftsapp.application.domain.exception.FileNotFoundException;
import eu.hyperspace.ftsapp.application.domain.model.InMemoryMultipartFile;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class FileTransferServiceImpl implements FileTransferService {

    private final MinioClient minioClient;

    @Value("${minio.bucketName}")
    private String bucketName;
    // до
    @Override
    public void uploadFile(MultipartFile file, String minioFileName) {
        try {
            createBucketIfNotExists();

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(minioFileName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        } catch (Exception e) {
            throw new FailedToUploadFileException(
                    "Failed to upload file " + minioFileName + " in minio: " + e.getMessage()
            );
        }
    }

    @Override
    public MultipartFile downloadFile(SFile sFile) {
        try {
            InputStream inputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(sFile.getMinioName())
                            .build());

            byte[] fileBytes = inputStream.readAllBytes();

            return new InMemoryMultipartFile(
                    sFile.getName(),
                    sFile.getName(),
                    sFile.getMimeType(),
                    fileBytes
            );
        } catch (Exception e) {
            throw new FailedToDownloadFileException("Failed to download file: " + e.getMessage());
        }
    }

    @Override
    public void deleteFile(String fileName) {
        try {
            if (!fileExists(fileName)) {
                throw new FileNotFoundException("File " + fileName + " does not exist");
            }

            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );
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
