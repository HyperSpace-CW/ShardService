package eu.hyperspace.ftsapp.controller;

import eu.hyperspace.ftsapp.exception.FailedToCreateBucketException;
import eu.hyperspace.ftsapp.exception.FailedToDeleteFileException;
import eu.hyperspace.ftsapp.exception.FailedToDownloadFileException;
import eu.hyperspace.ftsapp.exception.FailedToUpdateFileException;
import eu.hyperspace.ftsapp.exception.FailedToUploadFileException;
import eu.hyperspace.ftsapp.exception.FileNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return errors;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(FileNotFoundException.class)
    public String handleFileNotFoundException(FileNotFoundException ex) {
        return ex.getMessage();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(FailedToCreateBucketException.class)
    public String handleFailedToCreateBucketException(FailedToCreateBucketException ex) {
        return ex.getMessage();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(FailedToDownloadFileException.class)
    public String handleFailedToDownloadFileException(FailedToDownloadFileException ex) {
        return ex.getMessage();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(FailedToUpdateFileException.class)
    public String handleFailedToUpdateFileException(FailedToUpdateFileException ex) {
        return ex.getMessage();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(FailedToDeleteFileException.class)
    public String handleFailedToDeleteFileException(FailedToDeleteFileException ex) {
        return ex.getMessage();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(FailedToUploadFileException.class)
    public String handleFailedToUploadFileException(FailedToUploadFileException ex) {
        return ex.getMessage();
    }
}
