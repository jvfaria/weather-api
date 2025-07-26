package com.weatherapi.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ForecastProviderEnum {
    OPEN_METEO("open_meteo");

    private final String name;

    public static ForecastProviderEnum fromValue(String value) {
        for (ForecastProviderEnum e : ForecastProviderEnum.values()) {
            if (e.name.equalsIgnoreCase(value)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Unknown forecast provider: " + value);
    }
}
