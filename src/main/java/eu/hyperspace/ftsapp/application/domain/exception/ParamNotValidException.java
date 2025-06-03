package eu.hyperspace.ftsapp.application.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ParamNotValidException extends RuntimeException {
    public ParamNotValidException(String message) {
        super(message);
    }
}
