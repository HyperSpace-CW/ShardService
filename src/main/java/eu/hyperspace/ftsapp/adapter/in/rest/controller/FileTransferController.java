package eu.hyperspace.ftsapp.adapter.in.rest.controller;

import eu.hyperspace.ftsapp.adapter.in.rest.openapi.FileTransferControllerApi;
import eu.hyperspace.ftsapp.application.domain.dto.file.FileBase64DTO;
import eu.hyperspace.ftsapp.application.domain.dto.file.FileFullDataDTO;
import eu.hyperspace.ftsapp.application.domain.dto.file.FileNameDTO;
import eu.hyperspace.ftsapp.application.port.in.FileTransferService;
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
@RequestMapping("api/v1/shards/files")
@RequiredArgsConstructor
public class FileTransferController implements FileTransferControllerApi {

    private final FileTransferService fileTransferService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping()
    public FileFullDataDTO upload(@RequestBody @Valid FileFullDataDTO fileFullDataDTO) {
        return fileTransferService.uploadFile(fileFullDataDTO);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping()
    public FileFullDataDTO update(@RequestBody @Valid FileFullDataDTO fileFullDataDTO) {
        return fileTransferService.updateFile(fileFullDataDTO);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{fileName}")
    public FileBase64DTO downloadFile(@PathVariable @Valid String fileName) {
        return fileTransferService.downloadFile(fileName);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{fileName}")
    public FileNameDTO deleteFile(@PathVariable String fileName) {
        return fileTransferService.deleteFile(fileName);
    }
}
