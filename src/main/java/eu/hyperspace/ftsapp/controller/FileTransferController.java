package eu.hyperspace.ftsapp.controller;

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
public class FileTransferController {

    private final FileTransferService fileTransferService;

    @PostMapping("/upload")
    public ResponseEntity<FileFullDataDTO> upload(@RequestBody @Valid FileFullDataDTO fileFullDataDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(fileTransferService.uploadFile(fileFullDataDTO));
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<FileBase64DTO> downloadFile(@PathVariable @Valid String fileName) {
        return ResponseEntity.status(HttpStatus.OK).body(fileTransferService.downloadFile(fileName));
    }

    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<FileNameDTO> deleteFile(@PathVariable String fileName) {
        return ResponseEntity.status(HttpStatus.OK).body(fileTransferService.deleteFile(fileName));
    }
}
