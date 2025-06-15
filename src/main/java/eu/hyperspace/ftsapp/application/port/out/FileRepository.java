package eu.hyperspace.ftsapp.application.port.out;

import eu.hyperspace.ftsapp.application.domain.entity.SFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<SFile, Long> {
    List<SFile> findAllByShardId(Long shardId);

    Boolean existsByShardIdAndName(Long shardId, String name);

    @Query("SELECT f.name FROM SFile f WHERE f.shard.id = :shardId AND f.name IN :names")
    List<String> findExistingNamesInShard(
            @Param("shardId") Long shardId,
            @Param("names") Collection<String> names);
}
