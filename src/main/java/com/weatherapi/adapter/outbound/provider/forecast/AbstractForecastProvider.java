package com.weatherapi.adapter.outbound.provider.forecast;

import com.weatherapi.domain.dto.request.thirdparty.ForecastRequestDTO;
import com.weatherapi.domain.model.ForecastResponse;
import com.weatherapi.domain.port.ForecastProvider;

public abstract class AbstractForecastProvider implements ForecastProvider {
    public static final String CACHE_NAME = "forecastCache";

    protected abstract String buildCacheKey(ForecastRequestDTO request);

    protected abstract ForecastResponse doCall(ForecastRequestDTO request);
}
