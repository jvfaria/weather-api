package com.weatherapi.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.weatherapi.common.IntegrationTest;
import com.weatherapi.common.WiremockUtils;
import com.weatherapi.domain.dto.request.WeatherApiRequestDTO;
import com.weatherapi.domain.enums.TemperatureUnitScaleEnum;
import com.weatherapi.domain.exception.validation.FieldValidationError;
import com.weatherapi.domain.exception.validation.ValidationErrorDetails;
import com.weatherapi.domain.model.WeatherResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@AutoConfigureMockMvc
class WeatherFlowIT {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    private static HttpHeaders headers;
    private static ObjectMapper objectMapper;

    public static final String WEATHER_API = "/v1/api/weather";
    private String patternURL = "http://localhost:%s".concat(WEATHER_API);

    @BeforeAll
    static void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    @Test
    void shouldRetrieveWeatherFromZipcode() {
        WiremockUtils.geocodeFakeApiStubSuccess();
        WiremockUtils.forecastFakeApiStubSuccess();

        WeatherApiRequestDTO requestDTO = buildWeatherApiRequestDTO();

        patternURL = String.format(patternURL, port);

        String url = UriComponentsBuilder.fromUriString(patternURL)
                .queryParam("zipcode", requestDTO.getZipcode())
                .queryParam("startDate", requestDTO.getStartDate())
                .queryParam("endDate", requestDTO.getEndDate())
                .queryParam("unit", requestDTO.getUnit().toUpperCase()).build().toUriString();

        ResponseEntity<WeatherResponse> response = testRestTemplate
                .getForEntity(url, WeatherResponse.class);

        WeatherResponse body = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(body).isNotNull();
        assertThat(body.getCurrentTemperature()).isNotNull();
        assertThat(body.getHigh()).isEqualTo(Double.valueOf("16.1"));
        assertThat(body.getLow()).isEqualTo(Double.valueOf("16.0"));
        assertThat(body.getCity()).isEqualTo("Cupertino");
        assertThat(body.getTimezone()).isEqualTo("America/Los_Angeles");
    }

    @Test
    void shouldThrowMethodArgumentNotValidExceptionWhenMalformedDate() throws JsonProcessingException {
        WeatherApiRequestDTO requestDTO = buildWeatherApiRequestDTO();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        requestDTO.setStartDate(LocalDate.now().format(formatter));
        requestDTO.setEndDate(LocalDate.now().plusDays(1).format(formatter));

        patternURL = String.format(patternURL, port);

        String url = UriComponentsBuilder.fromUriString(patternURL)
                .queryParam("zipcode", requestDTO.getZipcode())
                .queryParam("startDate", requestDTO.getStartDate())
                .queryParam("endDate", requestDTO.getEndDate())
                .queryParam("unit", requestDTO.getUnit().toUpperCase()).build().toUriString();

        ResponseEntity<String> response = testRestTemplate.getForEntity(url, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("startDate");

        ValidationErrorDetails errorDetails = objectMapper.readValue(response.getBody(), ValidationErrorDetails.class);

        assertThat(errorDetails.getFields())
                .extracting(FieldValidationError::getField)
                .containsExactlyInAnyOrder("startDate", "endDate");
    }

    private WeatherApiRequestDTO buildWeatherApiRequestDTO() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return WeatherApiRequestDTO.builder()
                .zipcode(WiremockUtils.FAKE_ZIP_CODE)
                .unit(TemperatureUnitScaleEnum.CELSIUS.getName())
                .startDate(formatter.format(startDate))
                .endDate(formatter.format(endDate)).build();
    }

}
