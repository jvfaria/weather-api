package com.weatherapi.domain.port;

import com.weatherapi.domain.dto.request.WeatherApiRequestDTO;
import com.weatherapi.domain.dto.request.thirdparty.ForecastRequestDTO;
import com.weatherapi.domain.model.ForecastResponse;

public interface ForecastProvider {
    ForecastResponse getForecastFromLocation(WeatherApiRequestDTO request, String lat, String lon);

    String getProviderName();

    String buildForecastUrl(ForecastRequestDTO requestDTO);
}
