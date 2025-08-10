package com.weatherapi.integration.adapter;

import com.weatherapi.adapter.outbound.executor.ForecastApiExecutor;
import com.weatherapi.common.IntegrationTest;
import com.weatherapi.common.WiremockUtils;
import com.weatherapi.domain.dto.request.thirdparty.ForecastRequestDTO;
import com.weatherapi.domain.dto.response.ForecastOpenMeteoResponseDTO;
import com.weatherapi.domain.enums.TemperatureUnitScaleEnum;
import com.weatherapi.domain.mapper.forecast.ForecastOpenMeteoProviderMapper;
import com.weatherapi.domain.model.ForecastResponse;
import com.weatherapi.domain.service.CacheService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@IntegrationTest
class ForecastApiExecutorIT {
    public static final String FORECAST_CACHE_KEY = "forecast-cache-key";
    public static final String FORECAST_CACHE_NAME = "forecastCache";

    @Autowired
    ForecastApiExecutor forecastApiExecutor;

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    ForecastOpenMeteoProviderMapper mapper;

    @Autowired
    CacheService cacheService;

    @Value("${weatherapi.provider.forecast.meteo.baseurl}")
    String forecastBaseUrl;

    @Test
    @DisplayName("Should guarantee ForecastApiExecutor retries")
    void shouldExecuteSupplierWithRetry() {
        AtomicInteger count = new AtomicInteger();
        Supplier<String> supplier = () -> {
            count.incrementAndGet();
            throw new RuntimeException("API down");
        };

        assertThrows(RuntimeException.class, () -> forecastApiExecutor
                .executeWithCache("forestCache", "fail-key", String.class, supplier));

        assertThat(count.get()).isEqualTo(3);
    }

    @Test
    @DisplayName("Should set cache on response DTO when execute forecast external call")
    void shouldSetCacheOnForestResponseWhenExecutingSupplier() {
        WiremockUtils.forecastFakeApiStubSuccess();
        ForecastResponse forecastResponse = forecastApiExecutor.executeWithCache(FORECAST_CACHE_NAME, FORECAST_CACHE_KEY, ForecastResponse.class, this::doCall);

        assertThat(forecastResponse).isNotNull();
        assertThat(forecastResponse.getIsCached()).isFalse();
    }

    @DisplayName("Should set cache true on response DTO when execute external forecast call two times")
    @Test
    void shouldChangeCacheOnForestResponseWhenSameRequest() {
        WiremockUtils.forecastFakeApiStubSuccess();
        ForecastResponse firstCall = forecastApiExecutor.executeWithCache(FORECAST_CACHE_NAME, FORECAST_CACHE_KEY, ForecastResponse.class, this::doCall);

        assertThat(firstCall).isNotNull();
        assertThat(firstCall.getIsCached()).isFalse();

        // Assert cache manually
        Object cached = cacheService.get(FORECAST_CACHE_NAME, FORECAST_CACHE_KEY, Object.class);
        assertThat(cached).isNotNull();

        ForecastResponse secondCall = forecastApiExecutor.executeWithCache(FORECAST_CACHE_NAME, FORECAST_CACHE_KEY, ForecastResponse.class, this::doCall);
        assertThat(secondCall.getIsCached()).isTrue();
    }

    private ForecastResponse doCall() {
        ForecastOpenMeteoResponseDTO response = restTemplate
                .getForObject(buildForecastUrl(buildForecastRequest()), ForecastOpenMeteoResponseDTO.class);

        return mapper.toForecastResponse(response);
    }

    private String buildForecastUrl(ForecastRequestDTO requestDTO) {
        return UriComponentsBuilder.fromUriString(forecastBaseUrl)
                .queryParam("latitude", requestDTO.getLatitude())
                .queryParam("longitude", requestDTO.getLongitude())
                .queryParam("start_date", requestDTO.getStartDate())
                .queryParam("end_date", requestDTO.getEndDate())
                .queryParam("hourly", "temperature_2m")
                .queryParam("timezone", "auto").toUriString();
    }

    private ForecastRequestDTO buildForecastRequest() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return ForecastRequestDTO.builder()
                .zipcode("95014")
                .startDate(LocalDate.now().format(formatter))
                .endDate(LocalDate.now().plusDays(5).format(formatter))
                .unit(TemperatureUnitScaleEnum.CELSIUS.getName())
                .latitude("37.7749")
                .longitude("-122.4194").build();
    }


}
