package com.weatherapi.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TemperatureUnitScaleEnum {
    CELSIUS("celsius"),
    FAHRENHEIT("fahrenheit");

    private final String name;

    public static TemperatureUnitScaleEnum fromValue(String value) {
        for (TemperatureUnitScaleEnum e : TemperatureUnitScaleEnum.values()) {
            if (e.name.equalsIgnoreCase(value)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Unknown scale unit: " + value);
    }
}
