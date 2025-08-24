package com.weatherapi.unit;

import com.weatherapi.adapter.outbound.executor.GeocodeApiExecutor;
import com.weatherapi.adapter.outbound.provider.geocode.NominatimGeocodeProviderService;
import com.weatherapi.config.ProviderProperties;
import com.weatherapi.domain.dto.request.thirdparty.GeocodeRequestDTO;
import com.weatherapi.domain.mapper.geocode.NominatimLocationProviderMapper;
import com.weatherapi.domain.model.GeocodeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.function.Supplier;

import static com.weatherapi.common.TestDataFactory.buildProviderProperties;
import static com.weatherapi.common.WiremockUtils.GEOCODE_EXTENAL_FAKE_API;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class NominatimGeocodeProviderServiceTest {
    private NominatimGeocodeProviderService serviceSpy;
    private final String baseURL = "http://localhost:0".concat(GEOCODE_EXTENAL_FAKE_API);

    @Mock
    RestTemplate restTemplateMock;

    @Mock
    NominatimLocationProviderMapper mapperMock;

    @Mock
    GeocodeApiExecutor executorMock;

    @BeforeEach
    void setup() {
        ProviderProperties providerProperties = buildProviderProperties();
        providerProperties.getProvider().getGeocoding().getNominatim().setBaseUrl(baseURL);
        var realService = new NominatimGeocodeProviderService(restTemplateMock, mapperMock, providerProperties, executorMock);

        serviceSpy = Mockito.spy(realService);
    }

    @Test
    void shouldBuildGeocodeURLRequestWithAllRequiredParameters() {
        // arrange
        GeocodeRequestDTO geocodeRequestDTO = new GeocodeRequestDTO("95014");

        // act
        String url = serviceSpy.buildGeocodeUrl(geocodeRequestDTO);

        assertThat(url)
                .startsWith(baseURL)
                .contains("postalcode=95014")
                .contains("format=json")
                .contains("addressdetails=1")
                .contains("limit=1");
    }

    @Test
    void shouldGetRightProviderName() {
        // act
        String providerName = serviceSpy.getProviderName();

        assertThat(providerName).isEqualTo("nominatim");
    }

    @Test
    void shouldCallMethodDoCallThroughExecutorAndPassZipcode() {
        // arrange
        var expected = GeocodeResponse.builder().lat("-19.9").lon("-43.9").build();

        Mockito.when(executorMock.execute(any()))
                .thenAnswer(invocationOnMock -> ((Supplier<?>) invocationOnMock.getArgument(0)).get());
        Mockito.doReturn(expected).when(serviceSpy).doCall(any());

        // act
        GeocodeResponse geocodeOutResponse = serviceSpy.getLocation("95014");

        // assert
        assertThat(geocodeOutResponse).isEqualTo(expected);

        Mockito.verify(executorMock, Mockito.times(1)).execute(any());

        ArgumentCaptor<GeocodeRequestDTO> geocodeRequestCaptor = ArgumentCaptor.forClass(GeocodeRequestDTO.class);
        Mockito.verify(serviceSpy, Mockito.times(1)).doCall(geocodeRequestCaptor.capture());

        assertThat(geocodeRequestCaptor.getValue().getZipcode()).isEqualTo("95014");
    }

}
