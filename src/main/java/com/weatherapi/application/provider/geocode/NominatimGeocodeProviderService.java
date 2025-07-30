package com.weatherapi.application.provider.geocode;

import com.weatherapi.config.ProviderProperties;
import com.weatherapi.domain.dto.request.thirdparty.GeocodeRequestDTO;
import com.weatherapi.domain.dto.response.GeocodeNominatimResponseDTO;
import com.weatherapi.domain.enums.GeocodeProviderEnum;
import com.weatherapi.domain.exception.ResourceNotFoundException;
import com.weatherapi.domain.mapper.geocode.NominatimLocationProviderMapper;
import com.weatherapi.domain.model.GeocodeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class NominatimGeocodeProviderService extends AbstractGeocodeProvider {
    private final RestTemplate restTemplate;
    private final NominatimLocationProviderMapper mapper;
    private final ProviderProperties providerProperties;

    @Override
    protected GeocodeResponse doCall(GeocodeRequestDTO request) {
        GeocodeNominatimResponseDTO[] response = restTemplate.getForObject(buildGeocodeUrl(request), GeocodeNominatimResponseDTO[].class);

        if (response == null || response.length == 0) {
            throw ResourceNotFoundException.of(request.getZipcode());
        }

        return mapper.toGeocodeResponse(response[0]);
    }

    @Override
    public GeocodeResponse getLocation(String zipcode) {
        return null;
    }

    @Override
    public String getProviderName() {
        return GeocodeProviderEnum.NOMINATIM.getName();
    }

    @Override
    public String buildGeocodeUrl(GeocodeRequestDTO requestDTO) {
        String baseUrl = providerProperties.getProvider().getGeocoding().getNominatim().getBaseUrl();

        return UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("postalCode", requestDTO.getZipcode())
                .queryParam("format", "json")
                .queryParam("addressDetails", 1)
                .queryParam("limit", 1).build().toUriString();
    }

    @Override
    protected String buildCacheKey(GeocodeRequestDTO request) {
        // TODO might it be necessary to cache this request? Remember to analyze this after.
        return "";
    }
}
