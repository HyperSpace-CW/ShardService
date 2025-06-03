package eu.hyperspace.ftsapp.application.util.mapper;

import eu.hyperspace.ftsapp.application.domain.dto.shard.ShardCreationDto;
import eu.hyperspace.ftsapp.application.domain.dto.shard.ShardDto;
import eu.hyperspace.ftsapp.application.domain.dto.shard.ShardShortDto;
import eu.hyperspace.ftsapp.application.domain.dto.shard.ShardUpdateDto;
import eu.hyperspace.ftsapp.application.domain.entity.Shard;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ShardMapper {

    ShardDto toDto(Shard shard);

    ShardShortDto toShortDto(Shard shard);

    @Mapping(target = "name", source = "shardName")
    Shard createShardFromDto(ShardCreationDto shardCreationDto);


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "name", source = "shardName")
    void updateShardFromDto(ShardUpdateDto updateDto,
                            @MappingTarget Shard shard);


}
