package eu.hyperspace.ftsapp.application.port.out;

import eu.hyperspace.ftsapp.application.domain.entity.ShardUser;
import eu.hyperspace.ftsapp.application.domain.enums.AccessLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ShardUserRepository extends JpaRepository<ShardUser, ShardUser.ShardUserId> {

    @Query("SELECT CASE WHEN COUNT(su) > 0 THEN true ELSE false END " +
            "FROM ShardUser su " +
            "WHERE su.id.shardId = :shardId " +
            "AND su.id.userId = :userId "
    )
    Boolean existsByShardIdAndUserId(
            @Param("shardId")Long shardId,
            @Param("userId")Long userId);


    @Query("SELECT CASE WHEN COUNT(su) > 0 THEN true ELSE false END " +
            "FROM ShardUser su " +
            "WHERE su.id.shardId = :shardId " +
            "AND su.id.userId = :userId " +
            "AND su.accessLevel = :accessLevel")
    Boolean existsByIdAndAccessLevel(
            @Param("shardId")Long shardId,
            @Param("userId")Long userId,
            AccessLevel accessLevel);

}
