package eu.hyperspace.ftsapp.application.service;

import eu.hyperspace.ftsapp.application.domain.dto.shard.ShardCreationDto;
import eu.hyperspace.ftsapp.application.domain.dto.shard.ShardDto;
import eu.hyperspace.ftsapp.application.domain.dto.shard.ShardUpdateDto;
import eu.hyperspace.ftsapp.application.domain.entity.Shard;
import eu.hyperspace.ftsapp.application.domain.entity.ShardUser;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ShardServiceImpl implements ShardService {

    private final ShardRepository shardRepository;
    private final ShardUserRepository shardUserRepository;
    private final ShardMapper shardMapper;
    private final SecurityUtils securityUtils;

    @Transactional(readOnly = true)
    @Override
    public List<ShardDto> getUserShards() {
        return shardRepository
                .findAllByUserId(getCurrentUserId())
                .stream()
                .map(shardMapper::toDto).toList();
    }

    @Transactional
    @Override
    public void createShard(ShardCreationDto creationDto) {
        Shard newShard = shardMapper.createShardFromDto(creationDto);
        newShard.setOwnerId(getCurrentUserId());
        Shard savedShard = shardRepository.save(newShard);

        shardUserRepository.save(
                ShardUser.createOwner(savedShard, getCurrentUserId()));

    }

    @Transactional(readOnly = true)
    @Override
    public ShardDto getShardById(Long shardId) {
        checkAccess(shardId, getCurrentUserId(), AccessLevel.READ, AccessLevel.WRITE, AccessLevel.OWNER);

        return shardRepository.findById(shardId)
                .map(shardMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Shard", "ID"));
    }

    @Transactional
    @Override
    public void deleteShard(Long shardId) {
        checkAccess(shardId, getCurrentUserId(), AccessLevel.OWNER);

        shardRepository.deleteById(shardId);
    }


    @Transactional
    @Override
    public void updateShardInfo(Long shardId, ShardUpdateDto updateDto) {
        checkAccess(shardId, getCurrentUserId(), AccessLevel.WRITE, AccessLevel.OWNER);

        Shard shard = shardRepository.findById(shardId)
                .orElseThrow(() -> new EntityNotFoundException("Shard", "ID"));

        shardMapper.updateShardFromDto(updateDto, shard);

        shardRepository.save(shard);
    }

    private void checkAccess(Long shardId, Long userId,
                             AccessLevel... requiredLevels) {
        Set<AccessLevel> levels = requiredLevels.length > 0
                ? EnumSet.of(requiredLevels[0], requiredLevels)
                : EnumSet.allOf(AccessLevel.class);

        if (!shardUserRepository.existsByIdAndAccessLevelIn(shardId, userId,
                levels)) {
            throw new AccessDeniedException();
        }
    }

    private Long getCurrentUserId() {
        return securityUtils.getCurrentUserId();
    }

}
