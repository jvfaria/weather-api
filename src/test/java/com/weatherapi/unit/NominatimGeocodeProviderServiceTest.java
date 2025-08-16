package com.weatherapi.unit;

import com.weatherapi.adapter.outbound.executor.GeocodeApiExecutor;
import com.weatherapi.adapter.outbound.provider.geocode.NominatimGeocodeProviderService;
import com.weatherapi.config.ProviderProperties;
import com.weatherapi.domain.mapper.geocode.NominatimLocationProviderMapper;
import com.weatherapi.domain.model.GeocodeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class NominatimGeocodeProviderServiceTest {
    private NominatimGeocodeProviderService service;

    @Mock
    RestTemplate restTemplateMock;

    @Mock
    NominatimLocationProviderMapper mapperMock;

    @Mock
    ProviderProperties providerPropertiesMock;

    @Mock
    GeocodeApiExecutor executorMock;

    @BeforeEach
    void setup() {
        service = new NominatimGeocodeProviderService(restTemplateMock, mapperMock, providerPropertiesMock, executorMock);
    }

    @Test
    // TODO testes unitários do service
    void b() {
        // arrange


        // act
        GeocodeResponse location = service.getLocation("95014");

    }

}
