package eu.hyperspace.ftsapp.application.service;

import eu.hyperspace.ftsapp.application.domain.dto.shard.ShardCreationDto;
import eu.hyperspace.ftsapp.application.domain.dto.shard.ShardDto;
import eu.hyperspace.ftsapp.application.domain.dto.shard.ShardUpdateDto;
import eu.hyperspace.ftsapp.application.domain.entity.Shard;
import eu.hyperspace.ftsapp.application.domain.enums.AccessLevel;
import eu.hyperspace.ftsapp.application.domain.exception.AccessDeniedException;
import eu.hyperspace.ftsapp.application.port.out.ShardRepository;
import eu.hyperspace.ftsapp.application.port.out.ShardUserRepository;
import eu.hyperspace.ftsapp.application.util.SecurityUtils;
import eu.hyperspace.ftsapp.application.util.mapper.ShardMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Disabled
@ExtendWith(MockitoExtension.class)
class ShardServiceImplUnitTest {

    @Mock
    private ShardRepository shardRepository;

    @Mock
    private ShardUserRepository shardUserRepository;

    @Mock
    private ShardMapper shardMapper;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private ShardServiceImpl shardService;

    @Test
    void getUserShards_shouldReturnListOfShardsForCurrentUser() {
        // Arrange
        Long userId = 1L;
        Shard shard = new Shard();
        ShardDto shardDto = new ShardDto();

        when(securityUtils.getCurrentUserId()).thenReturn(userId);
        when(shardRepository.findAllByUserId(userId)).thenReturn(List.of(shard));
        when(shardMapper.toDto(shard)).thenReturn(shardDto);

        // Act
        List<ShardDto> result = shardService.getUserShards(0, 30, "all");

        // Assert
        assertEquals(1, result.size());
        assertEquals(shardDto, result.getFirst());
    }

    @Test
    void getUserShards_whenNoShards_shouldReturnEmptyList() {
        // Arrange
        Long userId = 1L;
        when(securityUtils.getCurrentUserId()).thenReturn(userId);
        when(shardRepository.findAllByUserId(userId)).thenReturn(Collections.emptyList());

        // Act
        List<ShardDto> result = shardService.getUserShards(0, 30, "all");

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void createShard_shouldSaveNewShardAndOwner() {
        // Arrange
        Long userId = 1L;
        ShardCreationDto creationDto = new ShardCreationDto();
        Shard newShard = new Shard();
        Shard savedShard = new Shard();

        when(securityUtils.getCurrentUserId()).thenReturn(userId);
        when(shardMapper.createShardFromDto(creationDto)).thenReturn(newShard);
        when(shardRepository.save(newShard)).thenReturn(savedShard);

        // Act
        shardService.createShard(creationDto);

        // Assert
        verify(shardRepository).save(newShard);
        verify(shardUserRepository).save(argThat(shardUser ->
                shardUser.getShard().equals(savedShard) &&
                        shardUser.getUserId().equals(userId) &&
                        shardUser.getAccessLevel() == AccessLevel.OWNER));
    }

    @Test
    void getShardById_whenHasAccess_shouldReturnShard() {
        // Arrange
        Long shardId = 1L;
        Long userId = 1L;
        Shard shard = new Shard();
        ShardDto shardDto = new ShardDto();

        when(securityUtils.getCurrentUserId()).thenReturn(userId);
        when(shardUserRepository
                .existsByIdAndAccessLevelIn(eq(shardId), eq(userId), any())).thenReturn(true);
        when(shardRepository.findById(shardId)).thenReturn(Optional.of(shard));
        when(shardMapper.toDto(shard)).thenReturn(shardDto);

        // Act
        ShardDto result = shardService.getShardById(shardId);

        // Assert
        assertEquals(shardDto, result);
    }

    @Test
    void getShardById_whenNoAccess_shouldThrowAccessDenied() {
        // Arrange
        Long shardId = 1L;
        Long userId = 1L;

        when(securityUtils.getCurrentUserId()).thenReturn(userId);
        doThrow(AccessDeniedException.class).when(shardUserRepository)
                .existsByIdAndAccessLevelIn(eq(shardId), eq(userId), any());

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> shardService.getShardById(shardId));
    }

    @Test
    void deleteShard_whenOwner_shouldDeleteShard() {
        // Arrange
        Long shardId = 1L;
        Long userId = 1L;

        when(securityUtils.getCurrentUserId()).thenReturn(userId);
        when(shardUserRepository
                .existsByIdAndAccessLevelIn(eq(shardId), eq(userId), any())).thenReturn(true);

        // Act
        shardService.deleteShard(shardId);

        // Assert
        verify(shardRepository).deleteById(shardId);
    }

    @Test
    void deleteShard_whenNotOwner_shouldThrowAccessDenied() {
        // Arrange
        Long shardId = 1L;
        Long userId = 1L;

        when(securityUtils.getCurrentUserId()).thenReturn(userId);
        doThrow(AccessDeniedException.class).when(shardUserRepository)
                .existsByIdAndAccessLevelIn(eq(shardId), eq(userId), any());

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> shardService.deleteShard(shardId));
        verify(shardRepository, never()).deleteById(any());
    }

    @Test
    void updateShardInfo_whenHasWriteAccess_shouldUpdateShard() {
        // Arrange
        Long shardId = 1L;
        Long userId = 1L;
        ShardUpdateDto updateDto = new ShardUpdateDto();
        Shard shard = new Shard();

        when(securityUtils.getCurrentUserId()).thenReturn(userId);
        when(shardUserRepository
                .existsByIdAndAccessLevelIn(eq(shardId), eq(userId), any())).thenReturn(true);
        when(shardRepository.findById(shardId)).thenReturn(Optional.of(shard));

        // Act
        shardService.updateShardInfo(shardId, updateDto);

        // Assert
        verify(shardMapper).updateShardFromDto(updateDto, shard);
        verify(shardRepository).save(shard);
    }

    @Test
    void updateShardInfo_whenNoAccess_shouldThrowAccessDenied() {
        // Arrange
        Long shardId = 1L;
        Long userId = 1L;
        ShardUpdateDto updateDto = new ShardUpdateDto();

        when(securityUtils.getCurrentUserId()).thenReturn(userId);
        doThrow(AccessDeniedException.class).when(shardUserRepository)
                .existsByIdAndAccessLevelIn(eq(shardId), eq(userId), any());

        // Act & Assert
        assertThrows(AccessDeniedException.class, () ->
                shardService.updateShardInfo(shardId, updateDto));
        verify(shardRepository, never()).save(any());
    }
}