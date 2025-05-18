package eu.hyperspace.ftsapp.adapter.in.rest.handler;

import eu.hyperspace.ftsapp.application.domain.dto.error.ErrorDto;
import eu.hyperspace.ftsapp.application.domain.exception.AccessDeniedException;
import eu.hyperspace.ftsapp.application.domain.exception.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class SecurityExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorDto handleEntityNotFound(EntityNotFoundException ex) {
        log.error(ex.getMessage());
        return new ErrorDto(HttpStatus.FORBIDDEN.value(), ex.getMessage());
    }
}
