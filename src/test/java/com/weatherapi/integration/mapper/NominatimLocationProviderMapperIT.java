package com.weatherapi.integration.mapper;

import com.weatherapi.common.IntegrationTest;
import com.weatherapi.domain.dto.response.GeocodeNominatimResponseDTO;
import com.weatherapi.domain.mapper.geocode.NominatimLocationProviderMapper;
import com.weatherapi.domain.model.Address;
import com.weatherapi.domain.model.GeocodeResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.weatherapi.common.TestDataFactory.buildNominatimGeocodeResponseDTO;
import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class NominatimLocationProviderMapperIT {

    @Autowired
    NominatimLocationProviderMapper mapper;

    @Test
    void shouldMapNominatimGeocodeResponseDTOToGeocodeResponse() {
        // arrange
        GeocodeNominatimResponseDTO dto = buildNominatimGeocodeResponseDTO();

        // act
        GeocodeResponse out = mapper.toGeocodeResponse(dto);

        // assert
        assertThat(out).isNotNull();
        assertThat(out.getLat()).isEqualTo(dto.getLat());
        assertThat(out.getLon()).isEqualTo(dto.getLon());

        Address inAddr = dto.getAddress();
        Address outAddr = out.getAddress();

        assertThat(outAddr).isNotNull();
        assertThat(outAddr.getCity()).isEqualTo(inAddr.getCity());
        assertThat(outAddr.getTown()).isEqualTo(inAddr.getTown());
        assertThat(outAddr.getStateDistrict()).isEqualTo(inAddr.getStateDistrict());
        assertThat(outAddr.getState()).isEqualTo(inAddr.getState());
        assertThat(outAddr.getPostcode()).isEqualTo(inAddr.getPostcode());
        assertThat(outAddr.getCountry()).isEqualTo(inAddr.getCountry());
        assertThat(outAddr.getCountryCode()).isEqualTo(inAddr.getCountryCode());
    }

    @Test
    void shouldHandleNullAddress() {
        GeocodeNominatimResponseDTO dto = GeocodeNominatimResponseDTO.builder()
                .lat("10.0000")
                .lon("-20.0000")
                .address(null)
                .build();

        GeocodeResponse out = mapper.toGeocodeResponse(dto);

        assertThat(out).isNotNull();
        assertThat(out.getLat()).isEqualTo("10.0000");
        assertThat(out.getLon()).isEqualTo("-20.0000");
        assertThat(out.getAddress()).isNull();
    }
}
