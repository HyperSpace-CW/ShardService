package eu.hyperspace.ftsapp.application.port.in;

import eu.hyperspace.ftsapp.application.domain.entity.SFile;
import org.springframework.web.multipart.MultipartFile;

public interface FileTransferService {

    void uploadFile(MultipartFile file, String minioFileName);

    MultipartFile downloadFile(SFile sFile);

    void deleteFile(String minioFileName);
}
