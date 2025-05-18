package eu.hyperspace.ftsapp.application.domain.exception;

public class FailedToDeleteFileException extends RuntimeException {
    public FailedToDeleteFileException() {
    }

    public FailedToDeleteFileException(String message) {
        super(message);
    }
}
