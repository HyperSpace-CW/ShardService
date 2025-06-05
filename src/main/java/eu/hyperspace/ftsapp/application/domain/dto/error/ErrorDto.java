package eu.hyperspace.ftsapp.application.domain.dto.error;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorDto {
    private Integer responseStatus;
    private String message;
}
