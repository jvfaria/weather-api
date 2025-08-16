package com.weatherapi.domain.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException of(String id) {
        String message = MessageResolver.getMessage("error.resource.not.found", id);

        return new ResourceNotFoundException(message);
    }
}
