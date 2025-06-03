package eu.hyperspace.ftsapp.application.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictStateException extends RuntimeException {
    public ConflictStateException(String message) {
        super(message);
    }
}
