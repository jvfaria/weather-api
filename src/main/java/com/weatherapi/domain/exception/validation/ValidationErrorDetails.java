package com.weatherapi.domain.exception.validation;

import com.weatherapi.domain.exception.ErrorDetails;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
public class ValidationErrorDetails extends ErrorDetails {
    List<FieldValidationError> fields;
}
