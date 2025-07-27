package com.weatherapi.domain.exception;

public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message);
    }

    public static BusinessRuleException of(String rule) {
        String message = MessageResolver.getMessage(rule);

        return new BusinessRuleException(message);
    }
}
