package eu.hyperspace.ftsapp.exception;

public class FailedToUpdateFileException extends RuntimeException {
    public FailedToUpdateFileException() {
    }

    public FailedToUpdateFileException(String message) {
        super(message);
    }
}
