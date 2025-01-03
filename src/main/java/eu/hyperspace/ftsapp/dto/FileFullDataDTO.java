package eu.hyperspace.ftsapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileFullDataDTO {
    @NotBlank(message = "Base64File does not have to be blank")
    private String base64File;

    @NotBlank(message = "FileName does not have to be blank")
    private String fileName;
}
