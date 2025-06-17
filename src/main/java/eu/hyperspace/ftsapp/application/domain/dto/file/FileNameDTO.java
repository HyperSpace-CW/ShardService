package eu.hyperspace.ftsapp.application.domain.dto.file;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Schema(description = "DTO для изменения названия файла")
@Data
@AllArgsConstructor
public class FileNameDTO {
    @NotBlank(message = "FileName does not have to be blank")
    @Size(max = 32, message = "FileName length must not exceed 32 characters")
    private String fileName;
}
