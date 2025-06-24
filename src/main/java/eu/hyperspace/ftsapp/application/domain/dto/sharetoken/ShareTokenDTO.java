package eu.hyperspace.ftsapp.application.domain.dto.sharetoken;

import eu.hyperspace.ftsapp.application.domain.enums.AccessLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Информация о токене доступа к шарду")
public class ShareTokenDTO {

    @Schema(description = "ID токена", example = "1")
    private Long id;

    @Schema(description = "Уровень доступа", example = "READ")
    private AccessLevel accessLevel;

    @Schema(description = "Токен для доступа", example = "550e8400-e29b-41d4-a716-446655440000")
    private String token;

    @Schema(description = "Дата создания токена", example = "2023-05-15T14:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "ID создателя токена", example = "123")
    private Long createdBy;

    @Schema(description = "ID шарда", example = "456")
    private Long shardId;
}
