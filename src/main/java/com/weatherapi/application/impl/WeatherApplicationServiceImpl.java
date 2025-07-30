package com.weatherapi.application.impl;

import com.weatherapi.application.WeatherApplicationService;
import com.weatherapi.application.WeatherOrchestratorService;
import com.weatherapi.domain.dto.request.WeatherApiRequestDTO;
import com.weatherapi.domain.model.WeatherResponse;
import com.weatherapi.domain.service.WeatherRequestValidatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherApplicationServiceImpl implements WeatherApplicationService {
    private final WeatherRequestValidatorService validator;
    private final WeatherOrchestratorService orchestratorService;

    @Override
    public WeatherResponse getWeatherByZipcodeAndDate(WeatherApiRequestDTO requestDTO) {
        validator.validateBusinessRules(requestDTO);
        WeatherResponse response = orchestratorService.handleRetrieveWeather(requestDTO);
        log.info("WeatherResponse generated for ZIP code {} | isCached={}", requestDTO.getZipcode(), response.getIsCached());

        return response;
    }
}
