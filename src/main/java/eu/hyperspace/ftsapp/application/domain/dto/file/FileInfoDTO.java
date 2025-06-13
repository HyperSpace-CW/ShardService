package eu.hyperspace.ftsapp.application.domain.dto.file;

import lombok.Data;

@Data
public class FileInfoDTO {
    private Long id;

    private String name;

    private String mimeType;

    private Long size;

    private Long shardId;
}
