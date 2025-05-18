package eu.hyperspace.ftsapp.application.domain.dto.shard;

import lombok.Data;

import java.util.Date;

@Data
public class ShardDto {
    private Long id;

    private String name;

    private String description;

    private Date updatedAt;

    private long ownerId;

    private long totalSize;

    private long fileCount;

}
