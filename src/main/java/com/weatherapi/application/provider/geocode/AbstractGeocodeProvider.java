package com.weatherapi.application.provider.geocode;

import com.weatherapi.domain.dto.request.thirdparty.GeocodeRequestDTO;
import com.weatherapi.domain.model.GeocodeResponse;

public abstract class AbstractGeocodeProvider implements GeocodeProvider {
    public static final String CACHE_NAME = "geocodeCache";

    protected abstract String buildCacheKey(GeocodeRequestDTO request);

    protected abstract GeocodeResponse doCall(GeocodeRequestDTO request);
}
