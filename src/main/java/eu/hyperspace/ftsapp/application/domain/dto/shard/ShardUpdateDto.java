package eu.hyperspace.ftsapp.application.domain.dto.shard;

import lombok.Data;

@Data
public class ShardUpdateDto {
    private String shardName;
    private String description;
}
