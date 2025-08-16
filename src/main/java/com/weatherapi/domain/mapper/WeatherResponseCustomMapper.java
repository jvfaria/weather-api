package com.weatherapi.domain.mapper;

import com.weatherapi.domain.model.ForecastResponse;
import com.weatherapi.domain.model.GeocodeResponse;
import com.weatherapi.domain.model.WeatherResponse;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class WeatherResponseCustomMapper {
    public WeatherResponse toWeatherResponse(GeocodeResponse geocode, ForecastResponse forecast) {
        return WeatherResponse.builder()
                .city(getCityOrTown(geocode))
                .latitude(geocode.getLat())
                .longitude(geocode.getLon())
                .displayName(buildDisplayName(geocode))
                .currentTemperature(forecast.getCurrentTemperature())
                .unit(forecast.getUnit())
                .high(forecast.getHigh())
                .low(forecast.getLow())
                .timezone(forecast.getTimezone())
                .offset(forecast.getOffset())
                .isCached(forecast.getIsCached())
                .address(geocode.getAddress())
                .hourly(forecast.getHourly())
                .build();
    }

    private static String getCityOrTown(GeocodeResponse geocode) {
        if (Objects.nonNull(geocode.getAddress())) {
            if (Objects.nonNull(geocode.getAddress().getCity())) {
                return geocode.getAddress().getCity();
            }

            if (Objects.nonNull(geocode.getAddress().getTown())) {
                return geocode.getAddress().getTown();
            }
        }
        return null;
    }

    private String buildDisplayName(GeocodeResponse geo) {
        if (geo.getAddress() == null) return null;
        return joinNotNull(
                geo.getAddress().getCity() == null ? geo.getAddress().getTown() : geo.getAddress().getCity(),
                geo.getAddress().getStateDistrict(),
                geo.getAddress().getState(),
                geo.getAddress().getPostalcode(),
                geo.getAddress().getCountry(),
                geo.getAddress().getCountryCode()
        );
    }

    public static String joinNotNull(String... parts) {
        return Arrays.stream(parts)
                .filter(s -> s != null && !s.trim().isEmpty())
                .collect(Collectors.joining(", "));
    }
}
