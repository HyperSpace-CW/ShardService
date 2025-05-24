package eu.hyperspace.ftsapp.application.domain.exception;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException() {
        super("You not have enough permissions to access this resource");
    }
}
