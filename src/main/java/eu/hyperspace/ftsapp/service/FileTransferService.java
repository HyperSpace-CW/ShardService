package eu.hyperspace.ftsapp.service;

import eu.hyperspace.ftsapp.dto.FileBase64DTO;
import eu.hyperspace.ftsapp.dto.FileNameDTO;
import eu.hyperspace.ftsapp.dto.FileFullDataDTO;

public interface FileTransferService {

    FileFullDataDTO uploadFile(FileFullDataDTO fileFullDataDTO);

    FileFullDataDTO updateFile(FileFullDataDTO fileFullDataDTO);

    FileBase64DTO downloadFile(String fileName);

    FileNameDTO deleteFile(String fileName);
}
