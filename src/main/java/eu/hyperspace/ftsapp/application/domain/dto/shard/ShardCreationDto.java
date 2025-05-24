package eu.hyperspace.ftsapp.application.domain.dto.shard;

import lombok.Data;

@Data
public class ShardCreationDto {
    private String shardName;
    private String description;
}
