package eu.hyperspace.ftsapp.adapter.in.rest.controller;

import eu.hyperspace.ftsapp.adapter.in.rest.openapi.FileControllerApi;
import eu.hyperspace.ftsapp.application.domain.dto.file.FileInfoDTO;
import eu.hyperspace.ftsapp.application.domain.dto.file.FileNameDTO;
import eu.hyperspace.ftsapp.application.port.in.FileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController implements FileControllerApi {

    private final FileService fileService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/get-by-shard")
    public List<FileInfoDTO> getShardFiles(
            @RequestParam Long shardId
    ) {
        return fileService.getShardFiles(shardId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping()
    public List<FileInfoDTO> uploadFiles(
            @RequestParam Long shardId,
            @RequestPart("files") List<MultipartFile> files
    ) {
        return fileService.uploadFiles(files, shardId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}")
    public FileInfoDTO updateFile(
            @RequestBody @Valid FileNameDTO fileNameDTO,
            @PathVariable Long id
    ) {
        return fileService.updateFile(fileNameDTO, id);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> downloadFiles(
            @RequestParam List<Long> fileIds
    ) {
        ByteArrayResource resource = fileService.downloadFiles(fileIds);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"files.zip\"")
                .contentType(MediaType.parseMediaType("application/zip"))
                .body(resource);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping()
    public List<FileInfoDTO> deleteFile(
            @RequestParam List<Long> fileIds
    ) {
        return fileService.deleteFiles(fileIds);
    }
}
