package eu.hyperspace.ftsapp.controller;

import eu.hyperspace.ftsapp.dto.FileBase64DTO;
import eu.hyperspace.ftsapp.dto.FileFullDataDTO;
import eu.hyperspace.ftsapp.dto.FileNameDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

public interface FileTransferController {
    @Operation(
            summary = "Загрузка файла в Minio",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Файл загружен", content = @Content(schema = @Schema(implementation = FileFullDataDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "500", description = "Ошибка на стороне сервера", content = @Content(schema = @Schema(implementation = String.class)))
            }
    )
    FileFullDataDTO upload(FileFullDataDTO fileFullDataDTO);

    @Operation(
            summary = "Обновление файла в Minio",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Файл обновлён", content = @Content(schema = @Schema(implementation = FileFullDataDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "404", description = "Файл не найден", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "500", description = "Ошибка на стороне сервера", content = @Content(schema = @Schema(implementation = String.class)))
            }
    )
    FileFullDataDTO update(FileFullDataDTO fileFullDataDTO);

    @Operation(
            summary = "Выгрузка файла из Minio",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Файл загружен", content = @Content(schema = @Schema(implementation = FileBase64DTO.class))),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "404", description = "Файл не найден", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "500", description = "Ошибка на стороне сервера", content = @Content(schema = @Schema(implementation = String.class)))
            }
    )
    FileBase64DTO downloadFile(String fileName);

    @Operation(
            summary = "Удаление файла из Minio",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Файл удалён", content = @Content(schema = @Schema(implementation = FileNameDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "404", description = "Файл не найден", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "500", description = "Ошибка на стороне сервера", content = @Content(schema = @Schema(implementation = String.class)))
            }
    )
    FileNameDTO deleteFile(String fileName);
}
