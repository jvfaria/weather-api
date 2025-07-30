package com.weatherapi.application.provider.geocode;

import com.weatherapi.domain.enums.GeocodeProviderEnum;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class GeocodeProviderFactory {
    private final Map<String, GeocodeProvider> providers;

    public GeocodeProviderFactory(Set<GeocodeProvider> providerSetList) {
        this.providers = providerSetList
                .stream().collect(Collectors.toMap(GeocodeProvider::getProviderName, Function.identity()));
    }

    public GeocodeProvider getProvider(String providerName) {
        // Fallback to get provider or get default nominatim provider
        return providers.getOrDefault(providerName, providers.get(GeocodeProviderEnum.NOMINATIM.getName()));
    }
}
