package eu.hyperspace.ftsapp.application.port.in;

import eu.hyperspace.ftsapp.application.domain.dto.file.FileInfoDTO;
import eu.hyperspace.ftsapp.application.domain.dto.file.FileNameDTO;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {
    List<FileInfoDTO> getShardFiles(Long shardId);

    List<FileInfoDTO> uploadFiles(List<MultipartFile> files, Long shardId);

    FileInfoDTO updateFile(FileNameDTO fileNameDTO, Long id);

    ByteArrayResource downloadFiles(List<Long> fileIds);

    List<FileInfoDTO> deleteFiles(List<Long> fileIds);
}
