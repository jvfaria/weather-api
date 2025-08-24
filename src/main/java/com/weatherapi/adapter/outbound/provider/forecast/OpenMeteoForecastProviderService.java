package com.weatherapi.adapter.outbound.provider.forecast;

import com.weatherapi.adapter.outbound.executor.ForecastApiExecutor;
import com.weatherapi.config.ProviderProperties;
import com.weatherapi.domain.dto.request.WeatherApiRequestDTO;
import com.weatherapi.domain.dto.request.thirdparty.ForecastRequestDTO;
import com.weatherapi.domain.dto.response.ForecastOpenMeteoResponseDTO;
import com.weatherapi.domain.enums.ForecastProviderEnum;
import com.weatherapi.domain.exception.BadGatewayException;
import com.weatherapi.domain.exception.InternalServerErrorException;
import com.weatherapi.domain.exception.ResourceNotFoundException;
import com.weatherapi.domain.mapper.forecast.ForecastOpenMeteoProviderMapper;
import com.weatherapi.domain.model.ForecastResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenMeteoForecastProviderService extends AbstractForecastProvider {
    private final ProviderProperties properties;
    private final RestTemplate restTemplate;
    private final ForecastOpenMeteoProviderMapper mapper;
    private final ForecastApiExecutor executor;

    @Override
    public ForecastResponse doCall(ForecastRequestDTO request) {
        ForecastOpenMeteoResponseDTO response = restTemplate.getForObject(buildForecastUrl(request), ForecastOpenMeteoResponseDTO.class);

        if (Objects.isNull(response)) {
            throw ResourceNotFoundException.of(request.getZipcode());
        }

        return mapper.toForecastResponse(response);
    }

    @Override
    public ForecastResponse getForecastFromLocation(WeatherApiRequestDTO apiRequestDTO, String lat, String lon) {
        ForecastRequestDTO request = buildForecastRequest(apiRequestDTO, lat, lon);

        try {
            return executor.executeWithCache(CACHE_NAME, buildCacheKey(request), ForecastResponse.class, () -> doCall(request));
        } catch (HttpServerErrorException ex) {
            log.error("External API (forecast provider) returned HTTP error: {}", ex.getStatusCode());
            throw new BadGatewayException("HTTP error occurred when calling the external forecast provider.", ex);
        } catch (Exception ex) {
            log.error("Unexpected internal error when fetching forecast.", ex);
            throw new InternalServerErrorException("Unexpected error while fetching weather forecast.", ex);
        }
    }

    @Override
    public String getProviderName() {
        return ForecastProviderEnum.OPEN_METEO.getName();
    }

    @Override
    public String buildForecastUrl(ForecastRequestDTO requestDTO) {
        String baseurl = properties.getProvider().getForecast().getMeteo().getBaseUrl();

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseurl)
                .queryParam("latitude", requestDTO.getLatitude())
                .queryParam("longitude", requestDTO.getLongitude())
                .queryParam("start_date", requestDTO.getStartDate())
                .queryParam("end_date", requestDTO.getEndDate())
                .queryParam("hourly", "temperature_2m")
                .queryParam("timezone", "auto");

        String unit = requestDTO.getUnit();
        if (unit != null) {
            builder.queryParam("temperature_unit", unit.toLowerCase());
        }

        return builder.build().toUriString();
    }

    @Override
    public String buildCacheKey(ForecastRequestDTO request) {
        return String.format("%s-%s-%s-%s", request.getZipcode(), request.getStartDate(), request.getEndDate(), request.getUnit());
    }

    private static ForecastRequestDTO buildForecastRequest(WeatherApiRequestDTO apiRequestDTO, String lat, String lon) {
        return ForecastRequestDTO.builder()
                .zipcode(apiRequestDTO.getZipcode())
                .latitude(lat)
                .longitude(lon)
                .startDate(apiRequestDTO.getStartDate())
                .endDate(apiRequestDTO.getEndDate())
                .unit(apiRequestDTO.getUnit() != null ? apiRequestDTO.getUnit() : null)
                .build();
    }

}
