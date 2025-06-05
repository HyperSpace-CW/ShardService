package eu.hyperspace.ftsapp.application.port.out;

import eu.hyperspace.ftsapp.application.domain.entity.Shard;
import eu.hyperspace.ftsapp.application.domain.enums.ShardState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ShardRepository extends JpaRepository<Shard, Long> {

    @Query("SELECT s FROM Shard s " +
            "JOIN ShardUser su ON s.id = su.shard.id " +
            "WHERE su.id.userId = :userId"
    )
    List<Shard> findAllByUserId(@Param("userId") Long userId);


    @Query("SELECT s FROM Shard s " +
            "WHERE s.ownerId = :ownerId " +
            "ORDER BY s.updatedAt DESC")
    Page<Shard> findPageByOwnerId(@Param("ownerId") Long ownerId, Pageable pageable);

    @Query("SELECT s FROM Shard s " +
            "WHERE s.ownerId = :ownerId AND s.state = :state " +
            "ORDER BY s.updatedAt DESC")
    Page<Shard> findByOwnerIdAndState(@Param("ownerId") Long ownerId,
                                      @Param("state") ShardState state,
                                      Pageable pageable);
}
