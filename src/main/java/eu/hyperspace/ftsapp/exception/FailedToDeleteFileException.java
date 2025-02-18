package eu.hyperspace.ftsapp.exception;

public class FailedToDeleteFileException extends RuntimeException {
    public FailedToDeleteFileException() {
    }

    public FailedToDeleteFileException(String message) {
        super(message);
    }
}
