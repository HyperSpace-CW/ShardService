package eu.hyperspace.ftsapp.application.util.mapper;

import eu.hyperspace.ftsapp.application.domain.dto.sharetoken.ShareTokenDTO;
import eu.hyperspace.ftsapp.application.domain.entity.Shard;
import eu.hyperspace.ftsapp.application.domain.entity.ShareToken;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ShareTokenMapper {

    @Mapping(target = "shardId", source = "shard", qualifiedByName = "shardToShardId")
    ShareTokenDTO toDto(ShareToken token);

    @Mapping(target = "shard", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    ShareToken toEntity(ShareTokenDTO dto);

    @Named("shardToShardId")
    default Long shardToShardId(Shard shard) {
        return shard.getId();
    }
}
