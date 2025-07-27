package com.weatherapi.domain.exception.validation;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FieldValidationError {
    private String field;
    private String message;
}
