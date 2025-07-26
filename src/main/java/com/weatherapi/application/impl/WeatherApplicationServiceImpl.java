package com.weatherapi.application.impl;

import com.weatherapi.application.WeatherApplicationService;
import com.weatherapi.domain.dto.request.WeatherApiRequestDTO;
import com.weatherapi.domain.model.WeatherResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherApplicationServiceImpl implements WeatherApplicationService {
    @Override
    public WeatherResponse getWeatherByZipcodeAndDate(WeatherApiRequestDTO requestDTO) {
        // TODO
        return null;
    }
}
