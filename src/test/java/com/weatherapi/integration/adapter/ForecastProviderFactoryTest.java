package com.weatherapi.integration.adapter;

import com.weatherapi.adapter.outbound.provider.forecast.ForecastProviderFactory;
import com.weatherapi.domain.enums.ForecastProviderEnum;
import com.weatherapi.domain.port.ForecastProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ForecastProviderFactoryTest {
    public static final String NON_EXISTENT_PROVIDER = "NON_EXISTENT_PROVIDER";
    public static final String OTHER_PROVIDER = "OTHER_PROVIDER";

    @Test
    @DisplayName("Falls back to OpenMeteo when provider name is unknown")
    void shouldFallbackToDefaultProviderWhenUnknown() {
        // Arrange
        ForecastProvider openMeteo = mock(ForecastProvider.class);
        when(openMeteo.getProviderName()).thenReturn(ForecastProviderEnum.OPEN_METEO.getName());
        ForecastProvider otherProvider = mock(ForecastProvider.class);
        when(otherProvider.getProviderName()).thenReturn(OTHER_PROVIDER);

        ForecastProviderFactory factory = new ForecastProviderFactory(Set.of(openMeteo, otherProvider));

        // assert
        assertThat(factory.getProvider(ForecastProviderEnum.OPEN_METEO.getName())).isSameAs(openMeteo);
        assertThat(factory.getProvider(OTHER_PROVIDER)).isSameAs(otherProvider);
        assertThat(factory.getProvider(NON_EXISTENT_PROVIDER)).isSameAs(openMeteo);
    }

}
