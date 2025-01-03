package eu.hyperspace.ftsapp.exception;

public class FailedToUploadFileException extends RuntimeException {
    public FailedToUploadFileException() {
    }

    public FailedToUploadFileException(String message) {
        super(message);
    }
}
