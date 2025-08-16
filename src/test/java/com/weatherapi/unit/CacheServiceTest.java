package com.weatherapi.unit;

import com.weatherapi.adapter.outbound.executor.ForecastApiExecutor;
import com.weatherapi.domain.model.ForecastResponse;
import com.weatherapi.domain.service.CacheService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CacheServiceTest {
    @Mock
    CacheService cacheService;

    @Test
    void returnsCacheAndSetsFlag() {
        ForecastApiExecutor executor = new ForecastApiExecutor(cacheService);
        ForecastResponse cachedForecastResponse = new ForecastResponse();
        when(cacheService.get("forecast", "forecast-cache-key", ForecastResponse.class)).thenReturn(cachedForecastResponse);


        ForecastResponse forecastResponse = executor.executeWithCache("forecast", "forecast-cache-key", ForecastResponse.class, () -> {
            throw new AssertionError();
        });

        assertThat(forecastResponse).isSameAs(cachedForecastResponse);
        assertThat(forecastResponse.getIsCached()).isTrue();
        verify(cacheService, never()).put(any(), any(), any());
    }

    @Test
    void missCachesAndSetsFlagFalse() {
        var exec = new ForecastApiExecutor(cacheService);
        when(cacheService.get(any(), any(), eq(ForecastResponse.class))).thenReturn(null);
        var fresh = new ForecastResponse();

        ForecastResponse forecastResponse = exec.executeWithCache("forecast", "k", ForecastResponse.class, () -> fresh);

        assertThat(forecastResponse).isSameAs(fresh);
        assertThat(forecastResponse.getIsCached()).isFalse();
        verify(cacheService).put("forecast", "k", fresh);
    }
}
