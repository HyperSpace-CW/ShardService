package eu.hyperspace.ftsapp.application.port.out;

import eu.hyperspace.ftsapp.application.domain.entity.Shard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShardRepository extends JpaRepository<Shard, Long> {
    List<Shard> findByOwnerId(Long ownerId);
}
