package com.weatherapi.integration.adapter;

import com.weatherapi.adapter.outbound.executor.GeocodeApiExecutor;
import com.weatherapi.common.IntegrationTest;
import com.weatherapi.common.WiremockUtils;
import com.weatherapi.domain.dto.response.GeocodeNominatimResponseDTO;
import com.weatherapi.domain.mapper.geocode.NominatimLocationProviderMapper;
import com.weatherapi.domain.model.GeocodeResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@IntegrationTest
class GeocodeApiExecutorIT {
    @Autowired
    GeocodeApiExecutor geocodeApiExecutor;

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    NominatimLocationProviderMapper mapper;

    @Value("${weatherapi.provider.geocoding.nominatim.baseurl}")
    String geocodeBaseUrl;

    @Test
    @DisplayName("Should guarantee GeocodeApiExecutor retries")
    void shouldRetrySupplier() {
        AtomicInteger count = new AtomicInteger();
        Supplier<String> supplier = () -> {
            count.incrementAndGet();
            throw new RuntimeException("API down");
        };

        assertThrows(RuntimeException.class, () -> geocodeApiExecutor.execute(supplier));

        System.out.println("Retry count: " + count.get());
        assert (count.get() == 3);
    }

    @Test
    @DisplayName("Should set cache on response DTO when execute external geocode call")
    void shouldSetCacheOnGeocodeResponseWhenExecutingSupplier() {
        WiremockUtils.geocodeFakeApiStubSuccess();
        GeocodeResponse geocodeResponse = geocodeApiExecutor.execute(this::doCall);

        assertThat(geocodeResponse).isNotNull();
        assertThat(geocodeResponse.getLat()).isNotBlank();
        assertThat(geocodeResponse.getLon()).isNotBlank();
        assertThat(geocodeResponse.getAddress()).isNotNull();
    }

    private GeocodeResponse doCall() {
        GeocodeNominatimResponseDTO[] response = restTemplate
                .getForObject(buildGeocodeUrl(), GeocodeNominatimResponseDTO[].class);

        return mapper.toGeocodeResponse(response[0]);
    }

    private String buildGeocodeUrl() {
        return UriComponentsBuilder.fromUriString(geocodeBaseUrl)
                .queryParam("postalcode", "94014")
                .queryParam("format", "json")
                .queryParam("addressdetails", 1)
                .queryParam("limit", 1).build().toUriString();
    }

}
