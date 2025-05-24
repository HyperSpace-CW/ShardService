package eu.hyperspace.ftsapp.application.port.in;

import eu.hyperspace.ftsapp.application.domain.dto.shard.ShardCreationDto;
import eu.hyperspace.ftsapp.application.domain.dto.shard.ShardDto;
import eu.hyperspace.ftsapp.application.domain.dto.shard.ShardUpdateDto;

import java.util.List;

public interface ShardService {
    List<ShardDto> getUserShards();

    void createShard(ShardCreationDto creationDto);

    ShardDto getShardById(Long shardId);

    void deleteShard(Long shardId);

    void updateShardInfo(Long shardId, ShardUpdateDto creationDto);
}
