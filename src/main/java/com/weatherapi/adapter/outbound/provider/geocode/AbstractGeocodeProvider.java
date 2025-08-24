package com.weatherapi.adapter.outbound.provider.geocode;

import com.weatherapi.domain.dto.request.thirdparty.GeocodeRequestDTO;
import com.weatherapi.domain.model.GeocodeResponse;
import com.weatherapi.domain.port.GeocodeProvider;

public abstract class AbstractGeocodeProvider implements GeocodeProvider {
    public static final String CACHE_NAME = "geocodeCache";

    public abstract String buildCacheKey(GeocodeRequestDTO request);

    public abstract GeocodeResponse doCall(GeocodeRequestDTO request);
}
