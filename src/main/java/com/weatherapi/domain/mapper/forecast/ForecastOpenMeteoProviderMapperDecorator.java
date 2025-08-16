package com.weatherapi.domain.mapper.forecast;

import com.weatherapi.domain.dto.response.ForecastOpenMeteoResponseDTO;
import com.weatherapi.domain.model.ForecastResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

@Component
public class ForecastOpenMeteoProviderMapperDecorator implements ForecastOpenMeteoProviderMapper {
    @Autowired
    @Qualifier("delegate")
    private ForecastOpenMeteoProviderMapper delegate;

    @Override
    public ForecastResponse toForecastResponse(ForecastOpenMeteoResponseDTO dto) {
        ForecastResponse response = delegate.toForecastResponse(dto);

        String unit = dto.getHourlyUnits().getUnit();
        if (unit != null) {
            response.setUnit(unit);
        }

        if (dto.getHourly() != null) {
            List<String> times = dto.getHourly().getTime();
            List<Double> temps = dto.getHourly().getTemperatures();

            if (times != null && temps != null && !times.isEmpty() && !temps.isEmpty()) {
                response.setCurrentTemperature(findCurrentTemp(times, temps));
                response.setHigh(temps.stream().max(Double::compare).orElse(null));
                response.setLow(temps.stream().min(Double::compare).orElse(null));
            }
        }
        return response;
    }

    // This method finds the current temperature based on the closest time from temp range
    private double findCurrentTemp(List<String> times, List<Double> temps) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        return IntStream.range(0, times.size())
                .boxed()
                .min(Comparator.comparingLong(i ->
                        Math.abs(Duration.between(now, LocalDateTime.parse(times.get(i), formatter)).toMinutes())
                ))
                .map(temps::get)
                .orElseThrow(() -> new IllegalArgumentException("No temperature data available"));
    }
}
