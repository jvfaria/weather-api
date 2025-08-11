package com.weatherapi.unit.application;

import com.weatherapi.adapter.outbound.provider.forecast.ForecastProviderFactory;
import com.weatherapi.adapter.outbound.provider.geocode.GeocodeProviderFactory;
import com.weatherapi.application.WeatherOrchestratorService;
import com.weatherapi.config.ProviderProperties;
import com.weatherapi.domain.dto.request.WeatherApiRequestDTO;
import com.weatherapi.domain.enums.ForecastProviderEnum;
import com.weatherapi.domain.enums.GeocodeProviderEnum;
import com.weatherapi.domain.mapper.WeatherResponseCustomMapper;
import com.weatherapi.domain.model.ForecastResponse;
import com.weatherapi.domain.model.GeocodeResponse;
import com.weatherapi.domain.model.WeatherResponse;
import com.weatherapi.domain.port.ForecastProvider;
import com.weatherapi.domain.port.GeocodeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.weatherapi.common.TestDataFactory.buildForecastResponse;
import static com.weatherapi.common.TestDataFactory.buildGeocodeResponse;
import static com.weatherapi.common.TestDataFactory.buildWeatherApiRequestDTO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class WeatherOrchestratorServiceTest {
    public static final String GEOCODE_PROVIDER_NAME = GeocodeProviderEnum.NOMINATIM.getName();
    public static final String FORECAST_PROVIDER_NAME = ForecastProviderEnum.OPEN_METEO.getName();

    @Mock
    GeocodeProviderFactory geocodeProviderFactory;

    @Mock
    ForecastProviderFactory forecastProviderFactory;

    @Mock
    GeocodeProvider geocodeProvider;

    @Mock
    ForecastProvider forecastProviderMock;

    @Mock
    WeatherResponseCustomMapper mapper;

    WeatherOrchestratorService orchestratorService;
    ProviderProperties providerProperties;

    @BeforeEach
    void setup() {
        providerProperties = new ProviderProperties();

        ProviderProperties.Provider.Geocoding geocodingProvider = new ProviderProperties.Provider.Geocoding();
        geocodingProvider.setDefaultProvider(GEOCODE_PROVIDER_NAME);

        ProviderProperties.Provider.Forecast forecastProvider = new ProviderProperties.Provider.Forecast();
        forecastProvider.setDefaultProvider(FORECAST_PROVIDER_NAME);

        ProviderProperties.Provider provider = new ProviderProperties.Provider();
        provider.setGeocoding(geocodingProvider);
        provider.setForecast(forecastProvider);

        providerProperties.setProvider(provider);

        orchestratorService = new WeatherOrchestratorService(providerProperties, geocodeProviderFactory, forecastProviderFactory, mapper);
    }

    @Test
    @DisplayName("Should retrieve weather data and successfully handle both forecast and weather API calls")
    void shouldRetrieveWeatherAndHandleForecastAndWeatherCallsWithSuccess() {
        // Arrange →
        GeocodeResponse geocodeResponse = buildGeocodeResponse();
        ForecastResponse forecastResponse = buildForecastResponse(geocodeResponse, false, 2);
        WeatherApiRequestDTO weatherApiRequestDTO = buildWeatherApiRequestDTO();
        WeatherResponse expected = new WeatherResponse();

        Mockito.when(geocodeProviderFactory.getProvider(GEOCODE_PROVIDER_NAME)).thenReturn(geocodeProvider);
        Mockito.when(forecastProviderFactory.getProvider(FORECAST_PROVIDER_NAME)).thenReturn(forecastProviderMock);

        Mockito.when(geocodeProvider.getLocation(weatherApiRequestDTO.getZipcode())).thenReturn(geocodeResponse);
        Mockito.when(forecastProviderMock.getForecastFromLocation(weatherApiRequestDTO, geocodeResponse.getLat(), geocodeResponse.getLon())).thenReturn(forecastResponse);

        Mockito.when(mapper.toWeatherResponse(geocodeResponse, forecastResponse)).thenReturn(expected);

        // Act →
        WeatherResponse result = orchestratorService.handleRetrieveWeather(weatherApiRequestDTO);

        // Assert ->
        assertThat(result).isSameAs(expected);

        ArgumentCaptor<String> zipcodeCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(geocodeProvider, Mockito.times(1)).getLocation(zipcodeCaptor.capture());
        String zipcodeCaptorValue = zipcodeCaptor.getValue();
        assertThat(weatherApiRequestDTO.getZipcode()).isEqualTo(zipcodeCaptorValue);

        ArgumentCaptor<String> latCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> lonCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(forecastProviderMock, Mockito.times(1)).getForecastFromLocation(eq(weatherApiRequestDTO), latCaptor.capture(), lonCaptor.capture());
        assertThat(latCaptor.getValue()).isEqualTo(geocodeResponse.getLat());
        assertThat(lonCaptor.getValue()).isEqualTo(geocodeResponse.getLon());

        Mockito.verify(mapper, Mockito.times(1)).toWeatherResponse(geocodeResponse, forecastResponse);
        Mockito.verifyNoMoreInteractions(geocodeProviderFactory, forecastProviderFactory, geocodeProvider, forecastProviderMock, mapper);
    }
}
