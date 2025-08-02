package com.weatherapi.domain.exception.validation;

import com.weatherapi.domain.exception.ErrorDetails;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ValidationErrorDetails extends ErrorDetails {
    List<FieldValidationError> fields;
}
