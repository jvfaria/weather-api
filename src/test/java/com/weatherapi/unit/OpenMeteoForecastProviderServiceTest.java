package com.weatherapi.unit;

import com.weatherapi.adapter.outbound.executor.ForecastApiExecutor;
import com.weatherapi.adapter.outbound.provider.forecast.OpenMeteoForecastProviderService;
import com.weatherapi.config.ProviderProperties;
import com.weatherapi.domain.dto.request.WeatherApiRequestDTO;
import com.weatherapi.domain.dto.request.thirdparty.ForecastRequestDTO;
import com.weatherapi.domain.mapper.forecast.ForecastOpenMeteoProviderMapper;
import com.weatherapi.domain.model.ForecastResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.function.Supplier;

import static com.weatherapi.common.TestDataFactory.ISO_DATE_FORMATTER;
import static com.weatherapi.common.TestDataFactory.buildForecastRequestDTO;
import static com.weatherapi.common.TestDataFactory.buildForecastResponse;
import static com.weatherapi.common.TestDataFactory.buildProviderProperties;
import static com.weatherapi.common.TestDataFactory.buildWeatherApiRequestDTO;
import static com.weatherapi.common.WiremockUtils.FORECAST_EXTENAL_FAKE_API;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class OpenMeteoForecastProviderServiceTest {

    private OpenMeteoForecastProviderService serviceSpy;
    private ProviderProperties providerProperties;
    private final String baseURL = "http://localhost:0".concat(FORECAST_EXTENAL_FAKE_API);

    @Mock
    RestTemplate restTemplateMock;

    @Mock
    ForecastOpenMeteoProviderMapper mapperMock;

    @Mock
    ForecastApiExecutor executorMock;

    @BeforeEach
    void setup() {
        providerProperties = buildProviderProperties();
        providerProperties.getProvider().getForecast().getMeteo().setBaseUrl(baseURL);

        var realService = new OpenMeteoForecastProviderService(
                providerProperties,
                restTemplateMock,
                mapperMock,
                executorMock
        );

        serviceSpy = Mockito.spy(realService);
    }

    @Test
    void shouldBuildForecastURLRequestWithAllRequiredParameters() {
        // arrange
        ForecastRequestDTO request = buildForecastRequestDTO();

        // act
        String url = serviceSpy.buildForecastUrl(request);

        // assert
        assertThat(url)
                .startsWith(baseURL)
                .contains("latitude=-19.9")
                .contains("longitude=-43.9")
                .contains("start_date=2025-08-01")
                .contains("end_date=2025-08-03")
                .contains("hourly=temperature_2m")
                .contains("timezone=auto")
                .contains("temperature_unit=celsius");
    }

    @Test
    void shouldGetRightProviderName() {
        // act
        String providerName = serviceSpy.getProviderName();

        // assert
        assertThat(providerName).isEqualTo(com.weatherapi.domain.enums.ForecastProviderEnum.OPEN_METEO.getName());
    }

    @Test
    void shouldCallMethodDoCallThroughExecutorAndPassRequestFields() {
        // arrange
        var expected = buildForecastResponse(false, 1);

        Mockito.when(executorMock.executeWithCache(
                        anyString(), anyString(), eq(ForecastResponse.class), any()))
                .thenAnswer(inv -> ((Supplier<?>) inv.getArgument(3)).get());
        Mockito.doReturn(expected).when(serviceSpy).doCall(any(ForecastRequestDTO.class));

        WeatherApiRequestDTO apiRequest = buildWeatherApiRequestDTO();

        String lat = "-19.9";
        String lon = "-43.9";

        // act
        ForecastResponse forecastOutResponse = serviceSpy.getForecastFromLocation(apiRequest, lat, lon);

        // assert
        assertThat(forecastOutResponse).isEqualTo(expected);

        Mockito.verify(executorMock, Mockito.times(1))
                .executeWithCache(anyString(), anyString(), eq(ForecastResponse.class), any());

        ArgumentCaptor<ForecastRequestDTO> reqCaptor = ArgumentCaptor.forClass(ForecastRequestDTO.class);
        Mockito.verify(serviceSpy, Mockito.times(1)).doCall(reqCaptor.capture());

        LocalDate now = LocalDate.now();

        var captured = reqCaptor.getValue();
        assertThat(captured.getZipcode()).isEqualTo("95014");
        assertThat(captured.getLatitude()).isEqualTo(lat);
        assertThat(captured.getLongitude()).isEqualTo(lon);
        assertThat(captured.getStartDate()).isEqualTo(now.format(ISO_DATE_FORMATTER));
        assertThat(captured.getEndDate()).isEqualTo(now.plusDays(1).format(ISO_DATE_FORMATTER));
        assertThat(captured.getUnit()).isEqualTo("celsius");

        ArgumentCaptor<String> cacheKeyCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(executorMock).executeWithCache(
                anyString(), cacheKeyCaptor.capture(), eq(ForecastResponse.class), any());

        assertThat(cacheKeyCaptor.getValue())
                .isEqualTo(String.format("95014-%s-%s-celsius", now.format(ISO_DATE_FORMATTER), now.plusDays(1).format(ISO_DATE_FORMATTER)));
    }
}
