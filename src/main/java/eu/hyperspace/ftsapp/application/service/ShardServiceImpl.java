package eu.hyperspace.ftsapp.application.service;

import eu.hyperspace.ftsapp.application.domain.dto.shard.ShardCreationDto;
import eu.hyperspace.ftsapp.application.domain.dto.shard.ShardDto;
import eu.hyperspace.ftsapp.application.domain.enums.AccessLevel;
import eu.hyperspace.ftsapp.application.domain.exception.AccessDeniedException;
import eu.hyperspace.ftsapp.application.domain.exception.EntityNotFoundException;
import eu.hyperspace.ftsapp.application.port.in.ShardService;
import eu.hyperspace.ftsapp.application.port.out.ShardRepository;
import eu.hyperspace.ftsapp.application.port.out.ShardUserRepository;
import eu.hyperspace.ftsapp.application.util.SecurityUtils;
import eu.hyperspace.ftsapp.application.util.mapper.ShardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShardServiceImpl implements ShardService {

    private final ShardRepository shardRepository;
    private final ShardUserRepository shardUserRepository;
    private final ShardMapper shardMapper;
    private final SecurityUtils securityUtils;

    @Override
    public List<ShardDto> getShardsByOwnerId(Long ownerId) {
        //TODO
        return List.of();
    }

    @Override
    public void createShard(Long ownerId, ShardCreationDto creationDto) {
        //TODO
    }

    @Override
    public ShardDto getShardById(Long shardId) {
        Long userId = securityUtils.getCurrentUserId();

        if(!shardUserRepository.existsByShardIdAndUserId(shardId, userId)) {
            throw new AccessDeniedException();
        }

        return shardRepository.findById(shardId)
                .map(shardMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Shard", "ID"));
    }

    @Override
    public void deleteShard(Long shardId) {
        Long userId = securityUtils.getCurrentUserId();

        if(!shardUserRepository.existsByIdAndAccessLevel(shardId, userId, AccessLevel.OWNER)) {
            throw new AccessDeniedException();
        }

        shardRepository.deleteById(shardId);
    }

    @Override
    public void updateShard(Long shardId, ShardCreationDto creationDto) {
        //TODO
    }
}
