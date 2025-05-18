package eu.hyperspace.ftsapp.application.domain.dto.file;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Schema(description = "DTO для отправки содержания файла")
@Data
@AllArgsConstructor
public class FileBase64DTO {

    @NotBlank(message = "FileBase64 does not have to be blank")
    private String fileBase64;
}
