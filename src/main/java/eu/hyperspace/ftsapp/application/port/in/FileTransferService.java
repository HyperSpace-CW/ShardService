package eu.hyperspace.ftsapp.application.port.in;

import eu.hyperspace.ftsapp.application.domain.dto.file.FileBase64DTO;
import eu.hyperspace.ftsapp.application.domain.dto.file.FileFullDataDTO;
import eu.hyperspace.ftsapp.application.domain.dto.file.FileNameDTO;

public interface FileTransferService {

    FileFullDataDTO uploadFile(FileFullDataDTO fileFullDataDTO);

    FileFullDataDTO updateFile(FileFullDataDTO fileFullDataDTO);

    FileBase64DTO downloadFile(String fileName);

    FileNameDTO deleteFile(String fileName);
}
