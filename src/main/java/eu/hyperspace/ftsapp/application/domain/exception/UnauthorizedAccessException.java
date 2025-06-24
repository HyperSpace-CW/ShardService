package eu.hyperspace.ftsapp.application.domain.exception;

public class UnauthorizedAccessException extends RuntimeException{

    public UnauthorizedAccessException(String entityName) {
        super(String.format("%s with this %s not found", entityName));
    }
}
