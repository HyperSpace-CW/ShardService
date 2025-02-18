package eu.hyperspace.ftsapp.exception;

public class FileAlreadyExistsException extends RuntimeException {
    public FileAlreadyExistsException() {
    }

    public FileAlreadyExistsException(String message) {
        super(message);
    }
}
