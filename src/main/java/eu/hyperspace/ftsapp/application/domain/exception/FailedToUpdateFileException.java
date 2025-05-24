package eu.hyperspace.ftsapp.application.domain.exception;

public class FailedToUpdateFileException extends RuntimeException {
    public FailedToUpdateFileException() {
    }

    public FailedToUpdateFileException(String message) {
        super(message);
    }
}
