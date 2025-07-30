package com.weatherapi.application;

import com.weatherapi.application.provider.forecast.ForecastProvider;
import com.weatherapi.application.provider.forecast.ForecastProviderFactory;
import com.weatherapi.application.provider.geocode.GeocodeProvider;
import com.weatherapi.application.provider.geocode.GeocodeProviderFactory;
import com.weatherapi.config.ProviderProperties;
import com.weatherapi.domain.dto.request.WeatherApiRequestDTO;
import com.weatherapi.domain.model.ForecastResponse;
import com.weatherapi.domain.model.GeocodeResponse;
import com.weatherapi.domain.model.WeatherResponse;
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

    protected WeatherResponse execute(WeatherApiRequestDTO requestDTO) {
        String geocodeProviderName = providerProperties.getProvider().getGeocoding().getDefaultProvider();
        GeocodeProvider geocodeProvider = geocodeProviderFactory.getProvider(geocodeProviderName);

        String forecastProviderName = providerProperties.getProvider().getForecast().getDefaultProvider();
        ForecastProvider forecastProvider = forecastProviderFactory.getProvider(forecastProviderName);

        GeocodeResponse location = fetchGeocodeResponse(requestDTO, geocodeProvider);
        ForecastResponse forecastResponse = fetchForecastResponse(requestDTO, forecastProvider, location);

        // TODO map response mapstruct
//        WeatherResponse weatherResponse =

        return null;


    }


    private static ForecastResponse fetchForecastResponse(WeatherApiRequestDTO requestDTO, ForecastProvider forecastProvider, GeocodeResponse location) {
        String lat = location.getLat();
        String lon = location.getLon();

        ForecastResponse forecastResponse = forecastProvider.getForecastFromLocation(requestDTO, lat, lon);
        log.info("Forecast response for location= lat:{} / lon:{} served from {}.", lat, lon, "PROVIDER");
        return forecastResponse;
    }

    private static GeocodeResponse fetchGeocodeResponse(WeatherApiRequestDTO requestDTO, GeocodeProvider geocodeProvider) {
        GeocodeResponse geocodeResponse = geocodeProvider.getLocation(requestDTO.getZipcode());
        log.info("Geocode response for ZIP={} served from {}.", requestDTO.getZipcode(), "PROVIDER");
        return geocodeResponse;
    }

}
