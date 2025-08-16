package com.weatherapi.integration.mapper;

import com.weatherapi.common.IntegrationTest;
import com.weatherapi.common.TestDataFactory;
import com.weatherapi.domain.dto.response.ForecastOpenMeteoResponseDTO;
import com.weatherapi.domain.mapper.forecast.ForecastOpenMeteoProviderMapper;
import com.weatherapi.domain.model.ForecastResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.weatherapi.common.TestDataFactory.CELSIUS_UNIT;
import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class ForecastOpenMeteoProviderMapperIT {

    @Autowired
    ForecastOpenMeteoProviderMapper mapper;

    @Test
    void shouldMapForecastOpenMeteoResponseDTOToForecastResponse() {
        // arrange
        ForecastOpenMeteoResponseDTO forecastOpenMeteoResponseDTO = TestDataFactory.buildForecastOpenMeteoResponseDTO();

        //act
        ForecastResponse forecastResponse = mapper.toForecastResponse(forecastOpenMeteoResponseDTO);

        // assert
        assertThat(forecastResponse.getUnit()).isEqualTo(CELSIUS_UNIT);
        assertThat(forecastResponse.getTimezone()).isEqualTo(forecastOpenMeteoResponseDTO.getTimezone());
        assertThat(forecastResponse.getOffset()).isEqualTo(forecastOpenMeteoResponseDTO.getOffset());

        assertThat(forecastResponse.getCurrentTemperature()).isNotNull();
        assertThat(forecastResponse.getHigh()).isNotNull();
        assertThat(forecastResponse.getLow()).isNotNull();


        assertThat(forecastResponse.getHourly()).hasSize(3);
        assertThat(forecastResponse.getHourly().get(0).getTime()).isEqualTo(forecastOpenMeteoResponseDTO.getHourly().getTime().getFirst());
        assertThat(forecastResponse.getHourly().get(0).getTemperature()).isEqualTo(forecastOpenMeteoResponseDTO.getHourly().getTemperatures().getFirst());
        assertThat(forecastResponse.getHourly().get(1).getTime()).isEqualTo(forecastOpenMeteoResponseDTO.getHourly().getTime().get(1));
        assertThat(forecastResponse.getHourly().get(1).getTemperature()).isEqualTo(forecastOpenMeteoResponseDTO.getHourly().getTemperatures().get(1));
        assertThat(forecastResponse.getHourly().get(2).getTime()).isEqualTo(forecastOpenMeteoResponseDTO.getHourly().getTime().get(2));
        assertThat(forecastResponse.getHourly().get(2).getTemperature()).isEqualTo(forecastOpenMeteoResponseDTO.getHourly().getTemperatures().get(2));
    }
}
