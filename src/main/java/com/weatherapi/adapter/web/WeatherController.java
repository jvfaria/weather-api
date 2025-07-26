package com.weatherapi.adapter.web;

import com.weatherapi.application.WeatherApplicationService;
import com.weatherapi.domain.dto.request.WeatherApiRequestDTO;
import com.weatherapi.domain.model.WeatherResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("${project.version}/api/weather")
@Tag(
        name = "Weather",
        description = "Endpoints for retrieving weather information by ZIP code."
)
@Slf4j
@RequiredArgsConstructor
public class WeatherController {
    private WeatherApplicationService weatherApplicationService;


    @GetMapping
    @Operation(
            summary = "Get weather by ZIP code and date range",
            description = "Retrieves weather information, including geocoding and current weather data, for the provided ZIP code and date range."
    )
    public ResponseEntity<WeatherResponse> getWeatherByZipCodeAndDate(@Valid WeatherApiRequestDTO request) {
        log.info("Received weather request: ZIP={}, start={}, end={}", request.getZipcode(), request.getStartDate(), request.getEndDate());

        WeatherResponse response = weatherApplicationService.getWeatherByZipcodeAndDate(request);
        return ResponseEntity.ok(response);
    }

}
