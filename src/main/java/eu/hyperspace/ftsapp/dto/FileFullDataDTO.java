package eu.hyperspace.ftsapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Schema(description = "DTO для отправки содержания файла и его названия")
@Data
@AllArgsConstructor
public class FileFullDataDTO {
    @NotBlank(message = "Base64File does not have to be blank")
    private String base64File;

    @NotBlank(message = "FileName does not have to be blank")
    private String fileName;
}
