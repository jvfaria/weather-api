package com.weatherapi.domain.mapper.forecast;

import com.weatherapi.domain.dto.response.ForecastOpenMeteoResponseDTO;
import com.weatherapi.domain.model.ForecastResponse;
import com.weatherapi.domain.model.HourlyForecast;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring", implementationName = "ForecastOpenMeteoProviderMapperImpl")
@DecoratedWith(ForecastOpenMeteoProviderMapperDecorator.class)
public interface ForecastOpenMeteoProviderMapper {
    ForecastResponse toForecastResponse(ForecastOpenMeteoResponseDTO dto);

    default List<HourlyForecast> mapHourly(ForecastOpenMeteoResponseDTO.Hourly hourly) {
        if (hourly == null || hourly.getTime() == null || hourly.getTemperatures() == null) return Collections.emptyList();
        List<HourlyForecast> result = new ArrayList<>();
        List<String> times = hourly.getTime();
        List<Double> temps = hourly.getTemperatures();
        for (int i = 0; i < times.size() && i < temps.size(); i++) {
            result.add(
                    HourlyForecast.builder()
                            .time(times.get(i))
                            .temperature(temps.get(i))
                            .build()
            );
        }
        return result;
    }
}
