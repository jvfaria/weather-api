package com.weatherapi.domain.validation;

import com.weatherapi.domain.dto.request.WeatherApiRequestDTO;
import com.weatherapi.domain.exception.validation.WeatherGetValidationException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

public class WeatherRequestValidator {
    private static final int MAX_DAYS_RANGE = 7;

    public void validateBusinessRules(WeatherApiRequestDTO dto) {
        LocalDate start;
        LocalDate end;
        try {
            start = LocalDate.parse(dto.getStartDate());
        } catch (DateTimeParseException ex) {
            throw WeatherGetValidationException.of("error.startDate.invalid");
        }
        try {
            end = LocalDate.parse(dto.getEndDate());
        } catch (DateTimeParseException ex) {
            throw WeatherGetValidationException.of("error.endDate.invalid");
        }
        if (start.isAfter(end)) {
            throw WeatherGetValidationException.of("error.dates.range.invalid");
        }
        long daysBetween = ChronoUnit.DAYS.between(start, end);
        if (daysBetween > MAX_DAYS_RANGE) {
            throw WeatherGetValidationException.of("error.dates.range.max7");
        }
    }
}
