package eu.hyperspace.ftsapp.exception;

public class FailedToDownloadFileException extends RuntimeException {
    public FailedToDownloadFileException() {
    }

    public FailedToDownloadFileException(String message) {
        super(message);
    }
}
