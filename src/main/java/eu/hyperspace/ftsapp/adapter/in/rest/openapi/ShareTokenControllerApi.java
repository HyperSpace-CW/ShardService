package eu.hyperspace.ftsapp.adapter.in.rest.openapi;

import eu.hyperspace.ftsapp.application.domain.dto.sharetoken.ShareTokenDTO;
import eu.hyperspace.ftsapp.application.domain.enums.AccessLevel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "Share Tokens", description = "Управление токенами доступа к шардам")
public interface ShareTokenControllerApi {

    @Operation(
            summary = "Создание токена доступа",
            description = "Создает токен для доступа к указанному шарду с заданным уровнем прав",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Токен успешно создан", content = @Content(schema = @Schema(implementation = ShareTokenDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Неверные параметры запроса"),
                    @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
                    @ApiResponse(responseCode = "404", description = "Шард не найден"),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
            }
    )
    @ResponseStatus(HttpStatus.CREATED)
    ShareTokenDTO createShareToken(Long shardId, AccessLevel accessLevel);

    @Operation(
            summary = "Чтение информации о токене",
            description = "Возвращает информацию о шарде, связанном с токеном",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Информация о токене", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "400", description = "Неверный формат токена"),
                    @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
                    @ApiResponse(responseCode = "404", description = "Токен не найден или истек"),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
            }
    )
    String readToken(String token);

    @Operation(
            summary = "Отзыв токенов доступа",
            description = "Отзывает все токены доступа для указанного шарда",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Токены успешно отозваны", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Недостаточно прав для операции"),
                    @ApiResponse(responseCode = "404", description = "Шард не найден"),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
            }
    )
    String revokeToken(Long shardId);
}