package eu.hyperspace.ftsapp.controller.impl;

import eu.hyperspace.ftsapp.controller.FileTransferController;
import eu.hyperspace.ftsapp.dto.FileBase64DTO;
import eu.hyperspace.ftsapp.dto.FileNameDTO;
import eu.hyperspace.ftsapp.dto.FileFullDataDTO;
import eu.hyperspace.ftsapp.service.FileTransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class FileTransferControllerImpl implements FileTransferController {

    private final FileTransferService fileTransferService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/upload")
    public FileFullDataDTO upload(@RequestBody @Valid FileFullDataDTO fileFullDataDTO) {
        return fileTransferService.uploadFile(fileFullDataDTO);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/update")
    public FileFullDataDTO update(@RequestBody @Valid FileFullDataDTO fileFullDataDTO) {
        return fileTransferService.updateFile(fileFullDataDTO);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/download/{fileName}")
    public FileBase64DTO downloadFile(@PathVariable @Valid String fileName) {
        return fileTransferService.downloadFile(fileName);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/delete/{fileName}")
    public FileNameDTO deleteFile(@PathVariable String fileName) {
        return fileTransferService.deleteFile(fileName);
    }
}
