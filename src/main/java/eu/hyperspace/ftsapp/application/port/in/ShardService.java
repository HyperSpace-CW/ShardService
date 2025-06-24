package eu.hyperspace.ftsapp.application.port.in;

import eu.hyperspace.ftsapp.application.domain.dto.shard.ShardCreationDto;
import eu.hyperspace.ftsapp.application.domain.dto.shard.ShardDto;
import eu.hyperspace.ftsapp.application.domain.dto.shard.ShardShortDto;
import eu.hyperspace.ftsapp.application.domain.dto.shard.ShardUpdateDto;
import eu.hyperspace.ftsapp.application.domain.entity.Shard;
import eu.hyperspace.ftsapp.application.domain.entity.ShardUser;
import eu.hyperspace.ftsapp.application.domain.enums.AccessLevel;
import jakarta.transaction.Transactional;

import java.util.List;

public interface ShardService {
    List<ShardDto> getUserShards(int page, int size, String category);

    ShardShortDto createShard(ShardCreationDto creationDto);

    ShardDto getShardById(Long shardId);

    void deleteShard(Long shardId);

    void updateShardInfo(Long shardId, ShardUpdateDto creationDto);

    Shard getShardEntityById(Long shardId);

    void updateShardSize(Long shardId, Long bytesCount);

    void updateShardFilesCount(Long shardId, Long filesDiff);

    boolean shardExistsById(Long shardId);

    boolean isOwner(Long shardId, Long userId);

    boolean hasAccess(Long shardId, Long userId, AccessLevel... requiredLevels);

    @Transactional
    ShardUser addShardUser(ShardUser shardUser);

    Long getCurrentUserId();

}
