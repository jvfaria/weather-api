package com.weatherapi.application;

import com.weatherapi.adapter.outbound.provider.forecast.ForecastProviderFactory;
import com.weatherapi.adapter.outbound.provider.geocode.GeocodeProviderFactory;
import com.weatherapi.config.ProviderProperties;
import com.weatherapi.domain.dto.request.WeatherApiRequestDTO;
import com.weatherapi.domain.mapper.WeatherResponseCustomMapper;
import com.weatherapi.domain.model.ForecastResponse;
import com.weatherapi.domain.model.GeocodeResponse;
import com.weatherapi.domain.model.WeatherResponse;
import com.weatherapi.domain.port.ForecastProvider;
import com.weatherapi.domain.port.GeocodeProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherOrchestratorService {
    private final ProviderProperties providerProperties;
    private final GeocodeProviderFactory geocodeProviderFactory;
    private final ForecastProviderFactory forecastProviderFactory;
    private final WeatherResponseCustomMapper mapper;

    public WeatherResponse handleRetrieveWeather(WeatherApiRequestDTO requestDTO) {
        String geocodeProviderName = providerProperties.getProvider().getGeocoding().getDefaultProvider();
        GeocodeProvider geocodeProvider = geocodeProviderFactory.getProvider(geocodeProviderName);

        String forecastProviderName = providerProperties.getProvider().getForecast().getDefaultProvider();
        ForecastProvider forecastProvider = forecastProviderFactory.getProvider(forecastProviderName);

        GeocodeResponse location = fetchGeocodeResponse(requestDTO, geocodeProvider);
        ForecastResponse forecastResponse = fetchForecastResponse(requestDTO, forecastProvider, location);

        return mapper.toWeatherResponse(location, forecastResponse);
    }

    private GeocodeResponse fetchGeocodeResponse(WeatherApiRequestDTO requestDTO, GeocodeProvider geocodeProvider) {
        GeocodeResponse geocodeResponse = geocodeProvider.getLocation(requestDTO.getZipcode());
        log.info("Geocode response for ZIP={} served from {}.", requestDTO.getZipcode(), "PROVIDER");
        return geocodeResponse;
    }

    private ForecastResponse fetchForecastResponse(WeatherApiRequestDTO requestDTO, ForecastProvider forecastProvider, GeocodeResponse location) {
        String lat = location.getLat();
        String lon = location.getLon();

        ForecastResponse forecastResponse = forecastProvider.getForecastFromLocation(requestDTO, lat, lon);
        log.info("Forecast response for location= lat:{} / lon:{} served from {}.", lat, lon, "PROVIDER");
        return forecastResponse;
    }

}
