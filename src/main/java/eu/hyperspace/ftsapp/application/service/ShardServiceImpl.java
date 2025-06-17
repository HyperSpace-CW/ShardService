package eu.hyperspace.ftsapp.application.service;

import eu.hyperspace.ftsapp.application.domain.dto.shard.ShardCreationDto;
import eu.hyperspace.ftsapp.application.domain.dto.shard.ShardDto;
import eu.hyperspace.ftsapp.application.domain.dto.shard.ShardShortDto;
import eu.hyperspace.ftsapp.application.domain.dto.shard.ShardUpdateDto;
import eu.hyperspace.ftsapp.application.domain.entity.Shard;
import eu.hyperspace.ftsapp.application.domain.entity.ShardUser;
import eu.hyperspace.ftsapp.application.domain.enums.AccessLevel;
import eu.hyperspace.ftsapp.application.domain.enums.ShardState;
import eu.hyperspace.ftsapp.application.domain.enums.params.ShardCategory;
import eu.hyperspace.ftsapp.application.domain.exception.AccessDeniedException;
import eu.hyperspace.ftsapp.application.domain.exception.ParamNotValidException;
import eu.hyperspace.ftsapp.application.domain.exception.ShardNotFoundException;
import eu.hyperspace.ftsapp.application.port.in.FileService;
import eu.hyperspace.ftsapp.application.port.in.ShardService;
import eu.hyperspace.ftsapp.application.port.out.ShardRepository;
import eu.hyperspace.ftsapp.application.port.out.ShardUserRepository;
import eu.hyperspace.ftsapp.application.util.SecurityUtils;
import eu.hyperspace.ftsapp.application.util.mapper.ShardMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private final ApplicationContext applicationContext;

    private FileService getFileService() {
        return applicationContext.getBean(FileService.class);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ShardDto> getUserShards(int page, int size, String category) {
        Pageable pageable = PageRequest.of(page, size);
        ShardCategory categoryEnum;

        try {
            categoryEnum = ShardCategory.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ParamNotValidException("Category not valid");
        }

        return getShardPageByCategory(pageable, categoryEnum);

    }

    public List<ShardDto> getShardPageByCategory(Pageable pageable, ShardCategory category) {
        Long userId = securityUtils.getCurrentUserId();

        Page<ShardDto> s = switch (category) {
            case ShardCategory.OWNED -> shardRepository.findPageByOwnerId(userId, pageable).map(shardMapper::toDto);
            case ShardCategory.ALL -> shardUserRepository.findAllUserShards(userId, pageable).map(shardMapper::toDto);
            case ShardCategory.TRASH ->
                    shardRepository.findByOwnerIdAndState(userId, ShardState.DELETED, pageable).map(shardMapper::toDto);
        };
        return s.get().toList();
    }


    @Transactional
    @Override
    public ShardShortDto createShard(ShardCreationDto creationDto) {
        Shard newShard = shardMapper.createShardFromDto(creationDto);
        newShard.setOwnerId(getCurrentUserId());
        Shard savedShard = shardRepository.save(newShard);

        shardUserRepository.save(
                ShardUser.createOwner(savedShard, getCurrentUserId()));

        return shardMapper.toShortDto(savedShard);
    }

    @Transactional(readOnly = true)
    @Override
    public ShardDto getShardById(Long shardId) {

        return shardRepository.findById(shardId)
                .map(shard -> {
                    checkAccess(shardId, getCurrentUserId(), AccessLevel.READ, AccessLevel.WRITE, AccessLevel.OWNER);
                    return shardMapper.toDto(shard);
                })
                .orElseThrow(() -> new ShardNotFoundException("Shard", "ID"));
    }

    /**
     * Удаляет связь пользователя с шардом в зависимости от его роли.
     *
     * <p>Для приглашённых пользователей просто удаляет связь с шардом.
     * Для владельца шарда сначала перемещает шард в корзину.
     * При повторном вызове метода помечает шард как удалённый и закрывает доступ всем пользователям.
     *
     * <p><b>Важно:</b> метод не выполняет физического удаления шарда из базы данных,
     * а только изменяет его статус.
     *
     * @see #deleteByState(Long)
     */
    @Transactional
    @Override
    public void deleteShard(Long shardId) {
        switch (getShardAccessLevel(shardId)) {
            case READ, WRITE -> {
                shardUserRepository.deleteById(shardId, getCurrentUserId());
            }
            case OWNER -> {
                deleteByState(shardId);
            }
        }
    }

    private void deleteByState(Long shardId) {
        Shard shard = shardRepository.findById(shardId)
                .orElseThrow(() -> new ShardNotFoundException("Shard", "ID"));

        switch (shard.getState()) {
            case ACTIVE -> {
                shard.setState(ShardState.IN_BIN);
                shardRepository.save(shard);
            }
            case IN_BIN -> {
                shard.setState(ShardState.DELETED);
                shardRepository.save(shard);
                shardUserRepository.deleteById(shardId, getCurrentUserId());
                getFileService().deleteFilesByShard(shard);
            }
            case DELETED -> throw new ShardNotFoundException("Shard", "ID");
        }
    }


    @Transactional
    @Override
    public void updateShardInfo(Long shardId, ShardUpdateDto updateDto) {
        checkAccess(shardId, getCurrentUserId(), AccessLevel.WRITE, AccessLevel.OWNER);

        Shard shard = shardRepository.findById(shardId)
                .orElseThrow(() -> new ShardNotFoundException("Shard", "ID"));

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

    private AccessLevel getShardAccessLevel(Long shardId) {
        return shardUserRepository.findById(shardId, getCurrentUserId())
                .orElseThrow(AccessDeniedException::new);
    }

    @Override
    public Shard getShardEntityById(Long shardId) {
        return shardRepository
                .findById(shardId)
                .orElseThrow(() -> new EntityNotFoundException("Shard with id " + shardId + " not found."));
    }

    @Override
    public void updateShardSize(Long shardId, Long bytesCount) {
        Shard shard = getShardEntityById(shardId);
        shard.setTotalSize(shard.getTotalSize() + bytesCount);
        shardRepository.save(shard);
    }

    @Override
    public boolean shardExistsById(Long shardId) {
        return shardRepository.existsById(shardId);
    }

}
