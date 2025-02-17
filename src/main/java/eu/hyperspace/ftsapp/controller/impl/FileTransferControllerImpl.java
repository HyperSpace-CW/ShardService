package eu.hyperspace.ftsapp.controller.impl;

import eu.hyperspace.ftsapp.controller.FileTransferController;
import eu.hyperspace.ftsapp.dto.FileBase64DTO;
import eu.hyperspace.ftsapp.dto.FileNameDTO;
import eu.hyperspace.ftsapp.dto.FileFullDataDTO;
import eu.hyperspace.ftsapp.service.FileTransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class FileTransferControllerImpl implements FileTransferController {

    private final FileTransferService fileTransferService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/file")
    public FileFullDataDTO upload(@RequestBody @Valid FileFullDataDTO fileFullDataDTO) {
        return fileTransferService.uploadFile(fileFullDataDTO);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/file")
    public FileFullDataDTO update(@RequestBody @Valid FileFullDataDTO fileFullDataDTO) {
        return fileTransferService.updateFile(fileFullDataDTO);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/file/{fileName}")
    public FileBase64DTO downloadFile(@PathVariable @Valid String fileName) {
        return fileTransferService.downloadFile(fileName);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/file/{fileName}")
    public FileNameDTO deleteFile(@PathVariable String fileName) {
        return fileTransferService.deleteFile(fileName);
    }
}
