package com.weatherapi.adapter.outbound.executor;

import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class GeocodeApiExecutor {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Retry(name = "geocodeRetry")
    public <T> T execute(Supplier<T> supplier) {
        log.info("[{}] Invoking external API call...", getClass().getSimpleName());
        return supplier.get();
    }

    private <T> T fallback(Supplier<T> supplier, Throwable t) {
        // TODO review this fallback method
        throw new RuntimeException("Nominatim API failed", t);
    }
}
