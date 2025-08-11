package com.weatherapi.common;

import com.github.javafaker.Faker;
import com.weatherapi.domain.dto.request.WeatherApiRequestDTO;
import com.weatherapi.domain.enums.TemperatureUnitScaleEnum;
import com.weatherapi.domain.model.ForecastResponse;
import com.weatherapi.domain.model.GeocodeResponse;
import com.weatherapi.domain.model.HourlyForecast;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.IntStream;

public class TestDataFactory {
    private static final Faker faker = new Faker();
    private static final DateTimeFormatter ISO_MINUTES = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    public static WeatherApiRequestDTO buildWeatherApiRequestDTO() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return WeatherApiRequestDTO.builder()
                .zipcode(WiremockUtils.FAKE_ZIP_CODE)
                .unit(TemperatureUnitScaleEnum.CELSIUS.getName())
                .startDate(formatter.format(startDate))
                .endDate(formatter.format(endDate)).build();
    }

    public static GeocodeResponse buildGeocodeResponse() {
        return GeocodeResponse.builder()
                .lat("37.7749")
                .lon("-122.4194").build();
    }

    public static ForecastResponse buildForecastResponse(GeocodeResponse location, boolean isCached, Integer hourlyLength) {
        return ForecastResponse.builder()
                .latitude(Double.parseDouble(location.getLon()))
                .longitude(Double.parseDouble(location.getLat()))
                .timezone("")
                .offset("America/Sao_paulo")
                .currentTemperature(28.8)
                .unit("GMT-3")
                .high(32.4)
                .low(21.0)
                .hourly(buildRandomHourlyForecastList(hourlyLength))
                .isCached(isCached).build();
    }

    private static HourlyForecast getHourlyForecast(LocalDateTime time, Double temperature) {
        return buildHourlyForecastObject(time, temperature);
    }

    private static List<HourlyForecast> buildRandomHourlyForecastList(Integer length) {
        if (length <= 0) {
            return List.of();
        }

        return IntStream
                .range(0, length)
                .mapToObj(ignored -> buildRandomHourlyForecastObject())
                .toList();
    }

    private static HourlyForecast buildRandomHourlyForecastObject() {
        return HourlyForecast.builder()
                .time(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).format(ISO_MINUTES))
                .temperature(faker.number().randomDouble(1, 1, 30)).build();
    }

    private static HourlyForecast buildHourlyForecastObject(LocalDateTime time, Double temperature) {
        return HourlyForecast.builder()
                .time(time.truncatedTo(ChronoUnit.MINUTES).format(ISO_MINUTES))
                .temperature(temperature).build();
    }
}
