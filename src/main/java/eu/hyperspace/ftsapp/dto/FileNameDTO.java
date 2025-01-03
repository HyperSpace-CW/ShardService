package eu.hyperspace.ftsapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileNameDTO {

    @NotBlank(message = "FileName does not have to be blank")
    private String fileName;
}
