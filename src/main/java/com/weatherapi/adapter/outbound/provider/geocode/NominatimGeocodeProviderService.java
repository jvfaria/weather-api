package com.weatherapi.adapter.outbound.provider.geocode;

import com.weatherapi.adapter.outbound.executor.GeocodeApiExecutor;
import com.weatherapi.config.ProviderProperties;
import com.weatherapi.domain.dto.request.thirdparty.GeocodeRequestDTO;
import com.weatherapi.domain.dto.response.GeocodeNominatimResponseDTO;
import com.weatherapi.domain.enums.GeocodeProviderEnum;
import com.weatherapi.domain.exception.BadGatewayException;
import com.weatherapi.domain.exception.InternalServerErrorException;
import com.weatherapi.domain.exception.ResourceNotFoundException;
import com.weatherapi.domain.mapper.geocode.NominatimLocationProviderMapper;
import com.weatherapi.domain.model.GeocodeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
@Slf4j
public class NominatimGeocodeProviderService extends AbstractGeocodeProvider {
    private final RestTemplate restTemplate;
    private final NominatimLocationProviderMapper mapper;
    private final ProviderProperties providerProperties;
    private final GeocodeApiExecutor executor;

    @Override
    public GeocodeResponse doCall(GeocodeRequestDTO request) {
        GeocodeNominatimResponseDTO[] response = restTemplate.getForObject(buildGeocodeUrl(request), GeocodeNominatimResponseDTO[].class);

        if (response == null || response.length == 0) {
            throw ResourceNotFoundException.of(request.getZipcode());
        }

        return mapper.toGeocodeResponse(response[0]);
    }

    @Override
    public GeocodeResponse getLocation(String zipcode) {
        try {
            return executor.execute(() -> doCall(new GeocodeRequestDTO(zipcode)));
        } catch (HttpServerErrorException ex) {
            log.error("External API (forecast provider) returned HTTP error: {}", ex.getStatusCode());
            throw new BadGatewayException("HTTP error occurred when calling the external geocode provider.", ex);
        } catch (ResourceNotFoundException ex) {
            log.warn("No geocode data found for zipcode {}: {}", zipcode, ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error while fetching geocode for zipcode {}: {}", zipcode, ex.getMessage(), ex);
            throw new InternalServerErrorException("Unexpected error while fetching geocode", ex);
        }
    }

    @Override
    public String getProviderName() {
        return GeocodeProviderEnum.NOMINATIM.getName();
    }

    @Override
    public String buildGeocodeUrl(GeocodeRequestDTO requestDTO) {
        String baseUrl = providerProperties.getProvider().getGeocoding().getNominatim().getBaseUrl();

        return UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("postalcode", requestDTO.getZipcode())
                .queryParam("format", "json")
                .queryParam("addressdetails", 1)
                .queryParam("limit", 1).build().toUriString();
    }

    @Override
    public String buildCacheKey(GeocodeRequestDTO request) {
        // TODO might it be necessary to cache this request? Remember to analyze this afterwards.
        return "";
    }
}
