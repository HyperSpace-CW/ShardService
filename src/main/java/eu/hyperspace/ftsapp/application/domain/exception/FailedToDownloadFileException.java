package eu.hyperspace.ftsapp.application.domain.exception;

public class FailedToDownloadFileException extends RuntimeException {
    public FailedToDownloadFileException() {
    }

    public FailedToDownloadFileException(String message) {
        super(message);
    }
}
