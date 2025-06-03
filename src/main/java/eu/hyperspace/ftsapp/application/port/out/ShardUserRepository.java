package eu.hyperspace.ftsapp.application.port.out;

import eu.hyperspace.ftsapp.application.domain.entity.Shard;
import eu.hyperspace.ftsapp.application.domain.entity.ShardUser;
import eu.hyperspace.ftsapp.application.domain.entity.ids.ShardUserId;
import eu.hyperspace.ftsapp.application.domain.enums.AccessLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface ShardUserRepository
        extends JpaRepository<ShardUser, ShardUserId> {

    @Query("SELECT CASE WHEN COUNT(su) > 0 THEN true ELSE false END " +
            "FROM ShardUser su " +
            "WHERE su.id.shardId = :shardId " +
            "AND su.id.userId = :userId " +
            "AND su.accessLevel IN :accessLevels")
    Boolean existsByIdAndAccessLevelIn(
            @Param("shardId") Long shardId,
            @Param("userId") Long userId,
            @Param("accessLevels") Set<AccessLevel> accessLevels);

    @Query("SELECT su.accessLevel " +
            "FROM ShardUser su " +
            "WHERE su.id.userId = :userId " +
            "AND su.id.shardId = :shardId")
    Optional<AccessLevel> findById(
            @Param("userId") Long userId,
            @Param("shardId") Long shardId);

    @Modifying
    @Query("DELETE FROM ShardUser su " +
            "WHERE su.id.shardId = :shardId " +
            "AND su.id.userId = :userId")
    void deleteById(Long shardId, Long currentUserId);


    @Query("SELECT su.shard FROM ShardUser su WHERE su.id.userId = :userId ORDER BY su.shard.updatedAt DESC")
    Page<Shard> findAllUserShards(@Param("userId") Long userId, Pageable pageable);
}
