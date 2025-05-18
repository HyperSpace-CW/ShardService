package eu.hyperspace.ftsapp.application.domain.dto.file;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Schema(description = "DTO для отправки названия файла")
@Data
@AllArgsConstructor
public class FileNameDTO {

    @NotBlank(message = "FileName does not have to be blank")
    private String fileName;
}
