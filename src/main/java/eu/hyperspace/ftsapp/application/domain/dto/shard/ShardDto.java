package eu.hyperspace.ftsapp.application.domain.dto.shard;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class ShardDto {
    private Long id;

    private String name;

    private String description;

    @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss")
    private Date updatedAt;

    private long ownerId;

    private long totalSize;

    private long fileCount;

}
