package eu.hyperspace.ftsapp.exception;

public class FailedToCreateBucketException extends RuntimeException {
    public FailedToCreateBucketException() {
    }

    public FailedToCreateBucketException(String message) {
        super(message);
    }
}
