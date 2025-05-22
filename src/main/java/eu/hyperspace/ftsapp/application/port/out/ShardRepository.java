package eu.hyperspace.ftsapp.application.port.out;

import eu.hyperspace.ftsapp.application.domain.entity.Shard;
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
}
