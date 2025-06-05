package eu.hyperspace.ftsapp.adapter.in.rest.handler;

import eu.hyperspace.ftsapp.application.domain.dto.error.ErrorDto;
import eu.hyperspace.ftsapp.application.domain.exception.ParamNotValidException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GeneralExceptionHandler {

    @ExceptionHandler(ParamNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto paramNotValidException(ParamNotValidException ex) {
        log.error(ex.getMessage());
        return new ErrorDto(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto inputDataInvalidException(IllegalArgumentException ex) {
        log.error(ex.getMessage());
        return new ErrorDto(HttpStatus.BAD_REQUEST.value(), "Input data is invalid");
    }

}
