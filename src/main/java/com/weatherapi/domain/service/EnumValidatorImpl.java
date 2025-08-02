package com.weatherapi.domain.service;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class EnumValidatorImpl implements ConstraintValidator<EnumValidator, String> {
    private Set<String> accepted;

    @Override
    public void initialize(EnumValidator annotation) {
        Class<? extends Enum<?>> enumClass = annotation.enumClass();
        accepted = Arrays.stream(enumClass.getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || accepted.contains(value.toUpperCase());
    }
}
