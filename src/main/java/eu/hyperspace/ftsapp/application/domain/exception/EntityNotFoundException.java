package eu.hyperspace.ftsapp.application.domain.exception;

import lombok.Getter;

@Getter
public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String entityName, String searchCriteria) {
        super(String.format("%s with this %s not found", entityName, searchCriteria));
    }



}
