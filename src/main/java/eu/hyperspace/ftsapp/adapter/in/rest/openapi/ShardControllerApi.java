package eu.hyperspace.ftsapp.adapter.in.rest.openapi;

import eu.hyperspace.ftsapp.application.domain.dto.shard.ShardCreationDto;
import eu.hyperspace.ftsapp.application.domain.dto.shard.ShardDto;
import eu.hyperspace.ftsapp.application.domain.dto.shard.ShardShortDto;
import eu.hyperspace.ftsapp.application.domain.dto.shard.ShardUpdateDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Shards", description = "Включает в себя все методы работы с шардами")
public interface ShardControllerApi {

    @Operation(
            summary = "Создание шарда",
            description = "Принимает на вход описание и название. Возвращает ID созданного шарда",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Шард создан", content = @Content(schema = @Schema(implementation = ShardShortDto.class))),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
                    @ApiResponse(responseCode = "500", description = "Ошибка на стороне сервера")
            }
    )
    ShardShortDto createShard(@RequestBody ShardCreationDto dto);

    @Operation(
            summary = "Получение списка шардов",
            description = "Отдаёт все шарды пользователя. Без указания параметров запроса отдаст последние 30 шардов, к которым обращался пользователь.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Шарды успешно получены", content = @Content(schema = @Schema(implementation = ShardDto.class))),
                    @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
                    @ApiResponse(responseCode = "500", description = "Ошибка на стороне сервера")
            }
    )
    List<ShardDto> getUserShards(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "30") int size,
                                 @RequestParam(defaultValue = "all") String category);


    @Operation(
            summary = "Получение шарда по его ID",
            description = "Отдаёт данные одного шарда.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Шард успешно получен", content = @Content(schema = @Schema(implementation = ShardDto.class))),
                    @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Нет доступа к шарду или шард был удалён ранее"),
                    @ApiResponse(responseCode = "500", description = "Ошибка на стороне сервера")
            }
    )
    ShardDto getShard(@PathVariable Long shardId);

    @Operation(
            summary = "Удаление шарда по его ID",
            description = "Если команду вызвывает создатель шарда - шард сначала переносится в корзину. Доступы забираются у всех, кроме владельца. При повторном вызове - шард удаляется полностью. Внимамние! В базе данных шард продолжит существовать, но обратиться к нему уже никто не сможет. Должно будет выбрасывать 403 ошибку при попытке получить доступ. Если вызывает пользователь с доступом read/write - просто забирается доступ.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Шард успешно получен"),
                    @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Нет доступа к шарду или шард уже удалён"),
                    @ApiResponse(responseCode = "500", description = "Ошибка на стороне сервера")
            }
    )
    void deleteShard(@PathVariable Long shardId);

    @Operation(
            summary = "Обновление шарда по его ID",
            description = "Принимает на вход данные для обновления",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Шард успешно изменён"),
                    @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Нет доступа к шарду или шард уже удалён"),
                    @ApiResponse(responseCode = "500", description = "Ошибка на стороне сервера")
            }
    )
    public void updateShardInfo(@PathVariable Long shardId, @RequestBody ShardUpdateDto dto);
}
