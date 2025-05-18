package eu.hyperspace.ftsapp.application.domain.exception;

public class FailedToCreateBucketException extends RuntimeException {
    public FailedToCreateBucketException() {
    }

    public FailedToCreateBucketException(String message) {
        super(message);
    }
}
