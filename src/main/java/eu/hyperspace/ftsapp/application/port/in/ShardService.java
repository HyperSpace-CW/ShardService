package eu.hyperspace.ftsapp.application.port.in;

import eu.hyperspace.ftsapp.application.domain.dto.shard.ShardCreationDto;
import eu.hyperspace.ftsapp.application.domain.dto.shard.ShardDto;

import java.util.List;

public interface ShardService {
    List<ShardDto> getShardsByOwnerId(Long ownerId);

    void createShard(Long ownerId, ShardCreationDto creationDto);

    ShardDto getShardById(Long shardId);

    void deleteShard(Long shardId);

    void updateShard(Long shardId, ShardCreationDto creationDto);
}
