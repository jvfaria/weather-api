package com.weatherapi.domain.exception.validation;

import com.weatherapi.domain.exception.MessageResolver;
import org.springframework.context.i18n.LocaleContextHolder;

public class WeatherGetValidationException extends RuntimeException {
    public WeatherGetValidationException(final String message) { super(message); }

    public static WeatherGetValidationException of(String rule) {
        String message = MessageResolver.getMessage(rule,
                new Object[]{rule},
                LocaleContextHolder.getLocale());

        return new WeatherGetValidationException(message);
    }
}
