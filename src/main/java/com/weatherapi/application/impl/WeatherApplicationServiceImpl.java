package com.weatherapi.application.impl;

import com.weatherapi.application.WeatherApplicationService;
import com.weatherapi.domain.dto.request.WeatherApiRequestDTO;
import com.weatherapi.domain.model.WeatherResponse;
import com.weatherapi.domain.validation.WeatherRequestValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherApplicationServiceImpl implements WeatherApplicationService {
    private WeatherRequestValidator validator;

    @Override
    public WeatherResponse getWeatherByZipcodeAndDate(WeatherApiRequestDTO requestDTO) {
        // TODO
        // TODO step 1 use validator
        validator.validateBusinessRules(requestDTO);
        // TODO step 2 call orchestrator

        // TODO step 3 return and handle response
        return null;
    }
}
