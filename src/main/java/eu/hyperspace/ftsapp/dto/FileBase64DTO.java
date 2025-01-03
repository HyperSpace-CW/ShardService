package eu.hyperspace.ftsapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileBase64DTO {

    @NotBlank(message = "FileBase64 does not have to be blank")
    private String fileBase64;
}
