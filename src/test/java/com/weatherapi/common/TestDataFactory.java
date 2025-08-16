package com.weatherapi.common;

import com.github.javafaker.Faker;
import com.weatherapi.domain.dto.request.WeatherApiRequestDTO;
import com.weatherapi.domain.dto.response.ForecastOpenMeteoResponseDTO;
import com.weatherapi.domain.dto.response.GeocodeNominatimResponseDTO;
import com.weatherapi.domain.enums.TemperatureUnitScaleEnum;
import com.weatherapi.domain.model.Address;
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
    public static final String AMERICA_SAO_PAULO_TIMEZONE = "America/Sao_paulo";
    public static final String CELSIUS_UNIT = "°C";
    public static final String OFFSET_GMT3 = "GMT-3";
    public static final String LAT = "37.7749";
    public static final String LON = "-122.4194";

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
                .lat(LAT)
                .lon(LON).build();
    }

    public static ForecastResponse buildForecastResponse(boolean isCached, Integer hourlyLength) {
        return ForecastResponse.builder()
                .timezone(AMERICA_SAO_PAULO_TIMEZONE)
                .offset(OFFSET_GMT3)
                .currentTemperature(28.8)
                .unit(CELSIUS_UNIT)
                .high(32.4)
                .low(21.0)
                .hourly(buildRandomHourlyForecastList(hourlyLength))
                .isCached(isCached).build();
    }

    public static ForecastOpenMeteoResponseDTO buildForecastOpenMeteoResponseDTO() {
        ForecastOpenMeteoResponseDTO.HourlyUnits hourlyUnits = new ForecastOpenMeteoResponseDTO.HourlyUnits();
        hourlyUnits.setTime("iso8601");
        hourlyUnits.setUnit(CELSIUS_UNIT);

        return ForecastOpenMeteoResponseDTO.builder()
                .timezone(AMERICA_SAO_PAULO_TIMEZONE)
                .offset(OFFSET_GMT3)
                .hourlyUnits(hourlyUnits)
                .hourly(buildHourlyObject()).build();

    }

    public static GeocodeNominatimResponseDTO buildNominatimGeocodeResponseDTO() {
        Address address = new Address();
        address.setCity(faker.address().city());
        address.setCountry(faker.address().country());
        address.setState(faker.address().state());
        address.setPostalcode(faker.address().zipCode());
        address.setStateDistrict(faker.address().stateAbbr());
        address.setTown(faker.address().city());
        address.setCountryCode(faker.address().countryCode());

        return GeocodeNominatimResponseDTO.builder().lat(LAT).lon(LON).address(address).build();
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

    private static ForecastOpenMeteoResponseDTO.Hourly buildHourlyObject() {
        ForecastOpenMeteoResponseDTO.Hourly hourly = new ForecastOpenMeteoResponseDTO.Hourly();

        List<String> times = List.of("2025-06-16T13:00", "2025-06-16T16:00", "2025-06-16T19:00");
        List<Double> temperatures = List.of(
                faker.number().randomDouble(1, 20, 30),
                faker.number().randomDouble(1, 22, 30),
                faker.number().randomDouble(1, 24, 30)
        );

        hourly.setTime(times);
        hourly.setTemperatures(temperatures);

        return hourly;
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
