package eu.hyperspace.ftsapp.adapter.in.rest.openapi;

import eu.hyperspace.ftsapp.application.domain.dto.file.FileInfoDTO;
import eu.hyperspace.ftsapp.application.domain.dto.file.FileNameDTO;
import eu.hyperspace.ftsapp.application.domain.dto.shard.ShardDto;
import eu.hyperspace.ftsapp.application.domain.dto.shard.ShardShortDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Files", description = "Включает в себя все методы работы с файлами")
public interface FileControllerApi {

    @Operation(
            summary = "Получение файлов по айди шарда",
            description = "Принимает айди шарда, возвращает список файлов с информацией о каждом из них.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список файлов получен", content = @Content(schema = @Schema(implementation = FileInfoDTO.class))),
                    @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
                    @ApiResponse(responseCode = "404", description = "Шард не найден"),
                    @ApiResponse(responseCode = "500", description = "Ошибка на стороне сервера")
            }
    )
    List<FileInfoDTO> getShardFiles(Long shardId);

    @Operation(
            summary = "Загрузка списка файлов в шард",
            description = "Принимает любое количество файлов и айди шарда, к которому надо прикрепить их.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Файлы загружены", content = @Content(schema = @Schema(implementation = FileInfoDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации"),
                    @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
                    @ApiResponse(responseCode = "404", description = "Шард не найден"),
                    @ApiResponse(responseCode = "500", description = "Ошибка на стороне сервера")
            }
    )
    List<FileInfoDTO> uploadFiles(Long shardId, List<MultipartFile> files);

    @Operation(
            summary = "Переименование шарда",
            description = "Принимает айди файла и новое имя файла",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Файл переименован", content = @Content(schema = @Schema(implementation = FileInfoDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации"),
                    @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
                    @ApiResponse(responseCode = "404", description = "Файл не найден"),
                    @ApiResponse(responseCode = "500", description = "Ошибка на стороне сервера")
            }
    )
    public FileInfoDTO updateFile(FileNameDTO fileNameDTO, Long id);

    @Operation(
            summary = "Скачивание файлов",
            description = "Принимает список айди файлов и скачивает их",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Файлы получены", content = @Content(
                            mediaType = "application/zip",
                            schema = @Schema(type = "string", format = "binary")
                    )),
                    @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
                    @ApiResponse(responseCode = "404", description = "Один или несколько файлов не найдены"),
                    @ApiResponse(responseCode = "500", description = "Ошибка на стороне сервера")
            }
    )
    public ResponseEntity<Resource> downloadFiles(List<Long> fileIds);

    @Operation(
            summary = "Удаление файлов",
            description = "Принимает список айди файлов и удаляет их",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Файлы удалены", content = @Content(schema = @Schema(implementation = FileInfoDTO.class))),
                    @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
                    @ApiResponse(responseCode = "404", description = "Один или несколько файлов не найдены"),
                    @ApiResponse(responseCode = "500", description = "Ошибка на стороне сервера")
            }
    )
    public List<FileInfoDTO> deleteFile(List<Long> fileIds);
}
