package com.weatherapi.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * DTO representing the response from the OpenMeteo forecast API.
 * Used only internally to map provider data to application models.
 */
@Getter
@Setter
public class ForecastOpenMeteoResponseDTO {
    @Schema(description = "Timezone of the forecast data.", example = "America/Sao_Paulo")
    private String timezone;

    @JsonProperty("timezone_abbreviation")
    @Schema(description = "Abbreviated timezone.", example = "GMT-3")
    private String offset;

    @JsonProperty("hourly_units")
    @Schema(description = "Units used for the hourly data.")
    private HourlyUnits hourlyUnits;

    @Schema(description = "Hourly forecast data.")
    private Hourly hourly;

    @Getter
    @Setter
    public static class HourlyUnits {
        @Schema(description = "Time format used in the response.", example = "iso8601")
        private String time;

        @JsonProperty("temperature_2m")
        @Schema(description = "Scale unit for temperature values.", example = "°C")
        private String unit;
    }

    @Getter
    @Setter
    public static class Hourly {
        @Schema(description = "List of times (ISO 8601 format) for each forecast entry.")
        private List<String> time;

        @JsonProperty("temperature_2m")
        @Schema(description = "Hourly temperature values for 2 meters above ground, matching the 'time' array.", example = "[16.1, 16.0, ...]")
        private List<Double> temperatures;
    }
}
