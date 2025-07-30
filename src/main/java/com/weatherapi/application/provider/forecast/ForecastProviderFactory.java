package com.weatherapi.application.provider.forecast;

import com.weatherapi.domain.enums.ForecastProviderEnum;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ForecastProviderFactory {
    private final Map<String, ForecastProvider> providers;

    public ForecastProviderFactory(Set<ForecastProvider> providerSetList) {
        this.providers = providerSetList
                .stream().collect(Collectors.toMap(ForecastProvider::getProviderName, Function.identity()));
    }

    public ForecastProvider getProvider(String providerName) {
        // Fallback to get provider or get default open meteo provider
        return providers.getOrDefault(providerName, providers.get(ForecastProviderEnum.OPEN_METEO.getName()));
    }
}
