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
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpServerErrorException;

import java.util.Set;

import static com.weatherapi.common.TestDataFactory.buildForecastResponse;
import static com.weatherapi.common.TestDataFactory.buildGeocodeResponse;
import static com.weatherapi.common.TestDataFactory.buildWeatherApiRequestDTO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WeatherOrchestratorServiceTest {
    public static final String GEOCODE_PROVIDER_NAME = GeocodeProviderEnum.NOMINATIM.getName();
    public static final String FORECAST_PROVIDER_NAME = ForecastProviderEnum.OPEN_METEO.getName();

    @Mock
    GeocodeProviderFactory geocodeProviderFactoryMock;

    @Mock
    ForecastProviderFactory forecastProviderFactoryMock;

    @Mock
    GeocodeProvider geocodeProviderMock;

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
        orchestratorService = new WeatherOrchestratorService(providerProperties, geocodeProviderFactoryMock, forecastProviderFactoryMock, mapper);
    }

    @Test
    @DisplayName("Should retrieve weather data and successfully handle geocode and forecast external API calls")
    void shouldRetrieveWeatherAndHandleForecastAndWeatherCallsWithSuccess() {
        // Arrange ->
        OrchestratorArrange orchestratorArrange = mockArrangeOrchestratorProviders();

        // Act ->
        WeatherResponse weatherResponse = orchestratorService.handleRetrieveWeather(orchestratorArrange.weatherApiRequestDTO());

        // Assert ->
        assertThat(weatherResponse).isSameAs(orchestratorArrange.expected());

        ArgumentCaptor<String> zipcodeCaptor = ArgumentCaptor.forClass(String.class);
        verify(geocodeProviderFactoryMock).getProvider(GEOCODE_PROVIDER_NAME);
        verify(geocodeProviderMock, times(1)).getLocation(zipcodeCaptor.capture());

        String zipcodeCaptorValue = zipcodeCaptor.getValue();
        assertThat(orchestratorArrange.weatherApiRequestDTO().getZipcode()).isEqualTo(zipcodeCaptorValue);

        ArgumentCaptor<String> latCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> lonCaptor = ArgumentCaptor.forClass(String.class);
        verify(forecastProviderFactoryMock).getProvider(FORECAST_PROVIDER_NAME);
        verify(forecastProviderMock, times(1)).getForecastFromLocation(eq(orchestratorArrange.weatherApiRequestDTO()), latCaptor.capture(), lonCaptor.capture());

        assertThat(latCaptor.getValue()).isEqualTo(orchestratorArrange.geocodeResponse().getLat());
        assertThat(lonCaptor.getValue()).isEqualTo(orchestratorArrange.geocodeResponse().getLon());


        ArgumentCaptor<GeocodeResponse> geocodeCaptor = ArgumentCaptor.forClass(GeocodeResponse.class);
        ArgumentCaptor<ForecastResponse> forecastCaptor = ArgumentCaptor.forClass(ForecastResponse.class);

        verify(mapper, times(1)).toWeatherResponse(geocodeCaptor.capture(), forecastCaptor.capture());
        assertThat(geocodeCaptor.getValue()).isSameAs(orchestratorArrange.geocodeResponse());
        assertThat(forecastCaptor.getValue()).isSameAs(orchestratorArrange.forecastResponse());

        verifyNoMoreInteractions(geocodeProviderFactoryMock, forecastProviderFactoryMock, geocodeProviderMock, forecastProviderMock, mapper);
    }

    @Test
    @DisplayName("Should throw exception when geocode provider fails to retrieve location")
    void shouldThrowExceptionWhenFailToFindLocation() {
        WeatherApiRequestDTO weatherApiRequestDTO = buildWeatherApiRequestDTO();

        when(geocodeProviderFactoryMock.getProvider(GEOCODE_PROVIDER_NAME)).thenReturn(geocodeProviderMock);
        when(forecastProviderFactoryMock.getProvider(FORECAST_PROVIDER_NAME)).thenReturn(forecastProviderMock);
        when(geocodeProviderMock.getLocation(any())).thenThrow(HttpServerErrorException.class);

        assertThrows(HttpServerErrorException.class, () -> orchestratorService.handleRetrieveWeather(weatherApiRequestDTO));
        verifyNoMoreInteractions(forecastProviderMock, mapper, forecastProviderFactoryMock);
    }

    @Test
    @DisplayName("Throws an exception if the forecast provider cannot retrieve forecast data")
    void shouldThrowExceptionWhenFailToFindForecast() {
        WeatherApiRequestDTO weatherApiRequestDTO = buildWeatherApiRequestDTO();

        // Arrange ->
        when(geocodeProviderFactoryMock.getProvider(GEOCODE_PROVIDER_NAME)).thenReturn(geocodeProviderMock);
        when(forecastProviderFactoryMock.getProvider(FORECAST_PROVIDER_NAME)).thenReturn(forecastProviderMock);
        when(geocodeProviderMock.getLocation(any())).thenReturn(new GeocodeResponse());
        when(forecastProviderMock.getForecastFromLocation(eq(weatherApiRequestDTO), any(), any())).thenThrow(HttpServerErrorException.class);

        // Assert ->
        assertThrows(HttpServerErrorException.class, () -> orchestratorService.handleRetrieveWeather(weatherApiRequestDTO));

        InOrder inOrder = inOrder(geocodeProviderMock, forecastProviderMock);
        inOrder.verify(geocodeProviderMock).getLocation(any());
        inOrder.verify(forecastProviderMock).getForecastFromLocation(eq(weatherApiRequestDTO), any(), any());

        verifyNoInteractions(mapper);
        verifyNoMoreInteractions(geocodeProviderFactoryMock, forecastProviderFactoryMock, geocodeProviderMock, forecastProviderMock);
    }

    @Test
    @DisplayName("Falls back to default provider when fail to find provider")
    void shouldFallbackViaFactoryWhenUnknownProvider() {
        providerProperties.getProvider().getGeocoding().setDefaultProvider("RANDOM_INEXISTENT_PROVIDER");
        providerProperties.getProvider().getForecast().setDefaultProvider("RANDOM_INEXISTENT_PROVIDER");

        // Arrange ->
        RealProvidersArrange arrange = arrangeRealProviderFactories();
        GeocodeResponse geocodeResponse = buildGeocodeResponse();
        ForecastResponse forecastResponse = buildForecastResponse(false, 2);

        when(arrange.geocodeProvider.getLocation(any())).thenReturn(geocodeResponse);
        when(arrange.forecastProvider.getForecastFromLocation(any(), any(), any())).thenReturn(forecastResponse);
        WeatherResponse expected = new WeatherResponse();
        when(mapper.toWeatherResponse(any(), any())).thenReturn(expected);

        orchestratorService = new WeatherOrchestratorService(providerProperties, arrange.geocodeProviderFactory, arrange.forecastProviderFactory, mapper);

        // Act ->
        WeatherResponse result = orchestratorService.handleRetrieveWeather(buildWeatherApiRequestDTO());

        WeatherApiRequestDTO req = buildWeatherApiRequestDTO();

        assertThat(result).isSameAs(expected);
        verify(mapper, times(1)).toWeatherResponse(any(), any());
        verify(arrange.geocodeProviderFactory).getProvider("RANDOM_INEXISTENT_PROVIDER");
        verify(arrange.forecastProviderFactory).getProvider("RANDOM_INEXISTENT_PROVIDER");
        verify(arrange.geocodeProvider).getLocation(req.getZipcode());
    }

    private static RealProvidersArrange arrangeRealProviderFactories() {
        GeocodeProvider nominatim = mock(GeocodeProvider.class);
        when(nominatim.getProviderName()).thenReturn(GeocodeProviderEnum.NOMINATIM.getName());

        GeocodeProviderFactory geocodeFactory = new GeocodeProviderFactory(Set.of(nominatim));
        GeocodeProviderFactory geocodeFactorySpy = spy(geocodeFactory);

        ForecastProvider openmeteo = mock(ForecastProvider.class);
        when(openmeteo.getProviderName()).thenReturn(ForecastProviderEnum.OPEN_METEO.getName());

        ForecastProviderFactory forecastFactory = new ForecastProviderFactory(Set.of(openmeteo));
        ForecastProviderFactory forecastFactorySpy = spy(forecastFactory);

        return new RealProvidersArrange(nominatim, openmeteo, geocodeFactorySpy, forecastFactorySpy);
    }

    private record RealProvidersArrange(GeocodeProvider geocodeProvider, ForecastProvider forecastProvider, GeocodeProviderFactory geocodeProviderFactory,
                                        ForecastProviderFactory forecastProviderFactory) {
    }

    private OrchestratorArrange mockArrangeOrchestratorProviders() {
        GeocodeResponse geocodeResponse = buildGeocodeResponse();
        ForecastResponse forecastResponse = buildForecastResponse(false, 2);
        WeatherApiRequestDTO weatherApiRequestDTO = buildWeatherApiRequestDTO();
        WeatherResponse expected = new WeatherResponse();

        when(geocodeProviderFactoryMock.getProvider(GEOCODE_PROVIDER_NAME)).thenReturn(geocodeProviderMock);
        when(forecastProviderFactoryMock.getProvider(FORECAST_PROVIDER_NAME)).thenReturn(forecastProviderMock);

        when(geocodeProviderMock.getLocation(weatherApiRequestDTO.getZipcode())).thenReturn(geocodeResponse);
        when(forecastProviderMock.getForecastFromLocation(weatherApiRequestDTO, geocodeResponse.getLat(), geocodeResponse.getLon())).thenReturn(forecastResponse);

        when(mapper.toWeatherResponse(geocodeResponse, forecastResponse)).thenReturn(expected);

        return new OrchestratorArrange(geocodeResponse, forecastResponse, weatherApiRequestDTO, expected);
    }

    private record OrchestratorArrange(GeocodeResponse geocodeResponse, ForecastResponse forecastResponse, WeatherApiRequestDTO weatherApiRequestDTO,
                                       WeatherResponse expected) {
    }

}
