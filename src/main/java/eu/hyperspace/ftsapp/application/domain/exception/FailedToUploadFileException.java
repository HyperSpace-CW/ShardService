package eu.hyperspace.ftsapp.application.domain.exception;

public class FailedToUploadFileException extends RuntimeException {
    public FailedToUploadFileException() {
    }

    public FailedToUploadFileException(String message) {
        super(message);
    }
}
