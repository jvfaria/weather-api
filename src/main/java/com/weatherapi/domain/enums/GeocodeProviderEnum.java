package com.weatherapi.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GeocodeProviderEnum {
    NOMINATIM("nominatim");

    private final String name;

    public static GeocodeProviderEnum fromValue(String value) {
        for (GeocodeProviderEnum e : GeocodeProviderEnum.values()) {
            if (e.name.equalsIgnoreCase(value)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Unknown geocode provider: " + value);
    }
}
