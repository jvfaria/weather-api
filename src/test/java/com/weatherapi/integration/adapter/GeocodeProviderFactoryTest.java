package com.weatherapi.integration.adapter;

import com.weatherapi.adapter.outbound.provider.geocode.GeocodeProviderFactory;
import com.weatherapi.domain.enums.GeocodeProviderEnum;
import com.weatherapi.domain.port.GeocodeProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GeocodeProviderFactoryTest {
    public static final String NON_EXISTENT_PROVIDER = "NON_EXISTENT_PROVIDER";
    public static final String OTHER_PROVIDER = "OTHER_PROVIDER";

    @Test
    @DisplayName("Falls back to Nominatim when provider name is unknown")
    void shouldFallbackToDefaultProviderWhenUnknown() {
        // Arrange
        GeocodeProvider nominatim = mock(GeocodeProvider.class);
        when(nominatim.getProviderName()).thenReturn(GeocodeProviderEnum.NOMINATIM.getName());
        GeocodeProvider other = mock(GeocodeProvider.class);
        when(other.getProviderName()).thenReturn(OTHER_PROVIDER);

        GeocodeProviderFactory factory = new GeocodeProviderFactory(Set.of(nominatim, other));

        // assert
        assertThat(factory.getProvider(OTHER_PROVIDER)).isSameAs(other);
        assertThat(factory.getProvider(GeocodeProviderEnum.NOMINATIM.getName())).isSameAs(nominatim);
        assertThat(factory.getProvider(NON_EXISTENT_PROVIDER)).isSameAs(nominatim);
    }

}
