package com.weatherapi.application.provider.geocode;

import com.weatherapi.domain.dto.request.thirdparty.GeocodeRequestDTO;
import com.weatherapi.domain.model.GeocodeResponse;

public interface GeocodeProvider {
    GeocodeResponse getLocation(String zipcode);

    String getProviderName();

    String buildGeocodeUrl(GeocodeRequestDTO requestDTO);
}
