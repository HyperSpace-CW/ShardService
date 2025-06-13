package eu.hyperspace.ftsapp.application.util.mapper;

import eu.hyperspace.ftsapp.application.domain.dto.file.FileInfoDTO;
import eu.hyperspace.ftsapp.application.domain.entity.SFile;
import eu.hyperspace.ftsapp.application.domain.entity.Shard;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface FileMapper {

    @Mapping(target = "shardId", source = "shard", qualifiedByName = "shardToShardId")
    FileInfoDTO toDto(SFile entity);

    @Mapping(target = "mimeType", source = "contentType")
    @Mapping(target = "name", source = "originalFilename")
    @Mapping(target = "size", source = "size")
    @Mapping(target = "minioName", expression = "java(generateMinioName())")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "shard", ignore = true)
    SFile toEntity(MultipartFile file);

    @Named("generateMinioName")
    default String generateMinioName() {
        return UUID.randomUUID().toString();
    }

    @Named("shardToShardId")
    default Long shardToShardId(Shard shard) {
        return shard.getId();
    }
}
