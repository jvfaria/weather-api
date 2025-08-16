package com.weatherapi.adapter.outbound.executor;

import com.weatherapi.domain.service.CacheService;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class ForecastApiExecutor {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    private final CacheService cacheService;

    @Retry(name = "forecastRetry")
    public <T> T executeWithCache(String cacheName, String cacheKey, Class<T> clazz, Supplier<T> supplier) {
        log.info("[{}] Invoking external API call...", getClass().getSimpleName());

        T cached = cacheService.get(cacheName, cacheKey, clazz);

        if (cached != null) {
            setIsCachedIfPossible(cached, true);
            log.info("[{}] HIT CACHE - {}:{}", getClass().getSimpleName(), cacheName, cacheKey);
            return cached;
        }

        T result = supplier.get();

        cacheService.put(cacheName, cacheKey, result);
        setIsCachedIfPossible(result, false);

        log.info("[{}] MISS CACHE - {}:{}", getClass().getSimpleName(), cacheName, cacheKey);

        return result;
    }

    private void setIsCachedIfPossible(Object obj, boolean isCached) {
        try {
            obj.getClass().getMethod("setIsCached", Boolean.class).invoke(obj, isCached);
        } catch (Exception ignored) {
        }
    }
}
