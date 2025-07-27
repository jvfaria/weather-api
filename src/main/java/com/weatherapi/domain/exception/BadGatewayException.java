package com.weatherapi.domain.exception;

public class BadGatewayException extends RuntimeException {
    public BadGatewayException(String message) {
        super(message);
    }

    public BadGatewayException(String message, Throwable cause) {
        super(message, cause);
    }
}
