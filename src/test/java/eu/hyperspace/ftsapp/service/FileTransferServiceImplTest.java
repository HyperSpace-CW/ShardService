package eu.hyperspace.ftsapp.service;

import eu.hyperspace.ftsapp.dto.FileBase64DTO;
import eu.hyperspace.ftsapp.dto.FileFullDataDTO;
import eu.hyperspace.ftsapp.dto.FileNameDTO;
import eu.hyperspace.ftsapp.exception.*;
import eu.hyperspace.ftsapp.service.impl.FileTransferServiceImpl;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.messages.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FileTransferServiceImplTest {
    @Mock
    private MinioClient minioClient;

    @InjectMocks
    private FileTransferServiceImpl fileTransferService;

    private final String bucketName = "test-bucket";

    private final String fileName = "file";

    private final String base64File = "aGVsbG8gd29ybGQ";

    private final String errorResponseCode = "NoSuchKey";

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(fileTransferService, "bucketName", bucketName);
    }

    @Test
    public void testUploadFile_Success() throws Exception {
        FileFullDataDTO fileFullDataDTO = new FileFullDataDTO(base64File, fileName);
        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(true);
        when(minioClient.statObject(any(StatObjectArgs.class))).thenThrow(new ErrorResponseException(
                new ErrorResponse(errorResponseCode, null, null, null, null, null, null),
                null, null));
        when(minioClient.putObject(any(PutObjectArgs.class))).thenReturn(null);

        FileFullDataDTO result = fileTransferService.uploadFile(fileFullDataDTO);

        assertEquals(fileFullDataDTO.getFileName(), result.getFileName());
        assertEquals(fileFullDataDTO.getBase64File(), result.getBase64File());
        verify(minioClient, times(1)).putObject(any(PutObjectArgs.class));
    }

    @Test
    public void testUploadFile_FileAlreadyExists() throws Exception {
        FileFullDataDTO fileFullDataDTO = new FileFullDataDTO(base64File, fileName);
        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(true);
        when(minioClient.statObject(any(StatObjectArgs.class))).thenReturn(null);

        assertThrows(FailedToUploadFileException.class, () -> fileTransferService.uploadFile(fileFullDataDTO));
        verify(minioClient, never()).putObject(any(PutObjectArgs.class));
    }

    @Test
    public void testUpdateFile_Success() throws Exception {
        FileFullDataDTO fileFullDataDTO = new FileFullDataDTO(base64File, fileName);
        when(minioClient.statObject(any(StatObjectArgs.class))).thenReturn(null);
        when(minioClient.putObject(any(PutObjectArgs.class))).thenReturn(null);

        FileFullDataDTO result = fileTransferService.updateFile(fileFullDataDTO);

        assertNotNull(result);
        assertEquals(fileFullDataDTO.getFileName(), result.getFileName());
        verify(minioClient, times(1)).putObject(any(PutObjectArgs.class));
    }

    @Test
    public void testUpdateFile_FileNotFound() throws Exception {
        FileFullDataDTO fileFullDataDTO = new FileFullDataDTO(base64File, fileName);
        when(minioClient.statObject(any(StatObjectArgs.class))).thenThrow(new ErrorResponseException(
                new ErrorResponse(errorResponseCode, null, null, null, null, null, null),
                null, null));

        assertThrows(FailedToUpdateFileException.class, () -> fileTransferService.updateFile(fileFullDataDTO));
        verify(minioClient, never()).putObject(any(PutObjectArgs.class));
    }

    @Test
    public void testDownloadFile_Success() throws Exception {
        InputStream inputStream = new ByteArrayInputStream(base64File.getBytes());

        GetObjectResponse getObjectResponse = mock(GetObjectResponse.class);
        when(getObjectResponse.readAllBytes()).thenReturn(base64File.getBytes());

        when(minioClient.statObject(any(StatObjectArgs.class))).thenReturn(null);
        when(minioClient.getObject(any(GetObjectArgs.class))).thenReturn(getObjectResponse);

        FileBase64DTO result = fileTransferService.downloadFile(fileName);

        assertNotNull(result);
        assertEquals(Base64.getEncoder().encodeToString(base64File.getBytes()), result.getFileBase64());
    }

    @Test
    public void testDownloadFile_FileNotFound() throws Exception {
        when(minioClient.statObject(any(StatObjectArgs.class))).thenThrow(new ErrorResponseException(
                new ErrorResponse(errorResponseCode, null, null, null, null, null, null),
                null, null));

        assertThrows(FailedToDownloadFileException.class, () -> fileTransferService.downloadFile(fileName));
    }

    @Test
    public void testDeleteFile_Success() throws Exception {
        when(minioClient.statObject(any(StatObjectArgs.class))).thenReturn(null);
        doNothing().when(minioClient).removeObject(any(RemoveObjectArgs.class));

        FileNameDTO result = fileTransferService.deleteFile(fileName);

        assertNotNull(result);
        assertEquals(fileName, result.getFileName());
        verify(minioClient, times(1)).removeObject(any(RemoveObjectArgs.class));
    }

    @Test
    public void testDeleteFile_FileNotFound() throws Exception {
        when(minioClient.statObject(any(StatObjectArgs.class))).thenThrow(new ErrorResponseException(
                new ErrorResponse(errorResponseCode, null, null, null, null, null, null),
                null, null));

        assertThrows(FailedToDeleteFileException.class, () -> fileTransferService.deleteFile(fileName));
        verify(minioClient, never()).removeObject(any(RemoveObjectArgs.class));
    }
}
