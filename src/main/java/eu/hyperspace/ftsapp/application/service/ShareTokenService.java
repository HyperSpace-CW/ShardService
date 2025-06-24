package eu.hyperspace.ftsapp.application.service;

import eu.hyperspace.ftsapp.application.domain.dto.sharetoken.ShareTokenDTO;
import eu.hyperspace.ftsapp.application.domain.entity.ShareToken;
import eu.hyperspace.ftsapp.application.domain.entity.Shard;
import eu.hyperspace.ftsapp.application.domain.entity.ShardUser;
import eu.hyperspace.ftsapp.application.domain.entity.ids.ShardUserId;
import eu.hyperspace.ftsapp.application.domain.enums.AccessLevel;
import eu.hyperspace.ftsapp.application.domain.exception.InvalidTokenException;
import eu.hyperspace.ftsapp.application.domain.exception.ParamNotValidException;
import eu.hyperspace.ftsapp.application.domain.exception.UnauthorizedAccessException;
import eu.hyperspace.ftsapp.application.port.in.ShardService;
import eu.hyperspace.ftsapp.application.port.out.ShardUserRepository;
import eu.hyperspace.ftsapp.application.port.out.ShareTokenRepository;
import eu.hyperspace.ftsapp.application.util.mapper.ShareTokenMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShareTokenService {

    private final ShareTokenRepository shareTokenRepository;
    private final ShardService shardService;
    private final ShardUserRepository shardUserRepository;
    private final ShareTokenMapper shareTokenMapper;

    @Transactional
    public ShareTokenDTO createToken(Long shardId, AccessLevel accessLevel) {
        Shard shard = shardService.getShardEntityById(shardId);
        Long currentUserId = shardService.getCurrentUserId();

        if (!shardService.isOwner(shardId, currentUserId)) {
            throw new ParamNotValidException("Only shard owner can create share tokens");
        }

        if (accessLevel == AccessLevel.OWNER) {
            throw new ParamNotValidException("You cant give OWNER access, only READ or WRITE");
        }

        ShareToken token = ShareToken.builder()
                .shard(shard)
                .accessLevel(accessLevel)
                .createdBy(currentUserId)
                .token(UUID.randomUUID().toString())
                .build();

        return shareTokenMapper.toDto(shareTokenRepository.save(token));
    }

    @Transactional
    public String readToken(String tokenValue) {
        Long currentUserId = shardService.getCurrentUserId();
        ShareToken token = shareTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new InvalidTokenException("Invalid share token"));
        Long shardId = token.getShard().getId();

        if (shardService.hasAccess(shardId, currentUserId, token.getAccessLevel())
                || (shardService.hasAccess(shardId, currentUserId, AccessLevel.WRITE)
                && token.getAccessLevel() == AccessLevel.READ)
        ) {
            throw new ParamNotValidException("You already have access with level " + token.getAccessLevel() +
                    " to shard with id " + shardId
            );
        }

        ShardUser shardUser = ShardUser.builder()
                .shard(token.getShard())
                .id(ShardUserId.of(token.getShard().getId(), currentUserId))
                .accessLevel(token.getAccessLevel())
                .build();

        shardService.addShardUser(shardUser);
        return "Access to shard with id " + shardId + " granted successfully";
    }

    @Transactional
    public String revokeAccesses(Long shardId) {
        Long currentUserId = shardService.getCurrentUserId();

        if (!shardService.isOwner(shardId, currentUserId)) {
            throw new ParamNotValidException("Only shard owner can revoke accesses");
        }

        shardUserRepository.deleteByShardIdAndAccessLevels(shardId, Set.of(AccessLevel.READ, AccessLevel.WRITE));
        shareTokenRepository.deleteByShardId(shardId);
        return "All accesses were revoked";
    }
}