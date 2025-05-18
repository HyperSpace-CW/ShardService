package eu.hyperspace.ftsapp.application.domain.exception;

public class FileAlreadyExistsException extends RuntimeException {
    public FileAlreadyExistsException() {
    }

    public FileAlreadyExistsException(String message) {
        super(message);
    }
}
