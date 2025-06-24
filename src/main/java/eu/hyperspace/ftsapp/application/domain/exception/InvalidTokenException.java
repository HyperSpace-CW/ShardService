package eu.hyperspace.ftsapp.application.domain.exception;

public class InvalidTokenException extends RuntimeException{

    public InvalidTokenException(String entityName) {
        super(String.format("%s with this %s not found", entityName));
    }
}
