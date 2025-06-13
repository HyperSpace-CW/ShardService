package eu.hyperspace.ftsapp.application.port.out;

import eu.hyperspace.ftsapp.application.domain.entity.SFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<SFile, Long> {
    List<SFile> findAllByShardId(Long shardId);

    Boolean existsByShardIdAndName(Long shardId, String name);
}
