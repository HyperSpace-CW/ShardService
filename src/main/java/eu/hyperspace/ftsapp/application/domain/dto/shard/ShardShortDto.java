package eu.hyperspace.ftsapp.application.domain.dto.shard;

import lombok.Data;

@Data
public class ShardShortDto {
    private Long id;

    private String name;

    private String description;

    private long ownerId;

}
