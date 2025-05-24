package eu.hyperspace.ftsapp.application.domain.dto.error;

public class ErrorDto {
    private Integer responseStatus;
    private String message;

    public ErrorDto(Integer responseStatus, String message) {
    }
}
