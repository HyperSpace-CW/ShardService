package eu.hyperspace.ftsapp.adapter.in.rest.handler;

import eu.hyperspace.ftsapp.application.domain.dto.error.ErrorDto;
import eu.hyperspace.ftsapp.application.domain.exception.ConflictStateException;
import eu.hyperspace.ftsapp.application.domain.exception.ShardNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ShardExceptionHandler {

    @ExceptionHandler(ShardNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDto handleShardNotFound(ShardNotFoundException ex) {
        log.error(ex.getMessage());
        return new ErrorDto(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }

    @ExceptionHandler(ConflictStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorDto handleConflictStateException(ConflictStateException ex) {
        log.error(ex.getMessage());
        return new ErrorDto(HttpStatus.CONFLICT.value(), ex.getMessage());
    }
}
