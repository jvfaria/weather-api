package com.weatherapi.application;

import com.weatherapi.domain.dto.request.WeatherApiRequestDTO;
import com.weatherapi.domain.model.WeatherResponse;

public interface WeatherApplicationService {
    WeatherResponse getWeatherByZipcodeAndDate(WeatherApiRequestDTO requestDTO);
}
