package com.weatherapi.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Hourly forecast entry.")
public class HourlyForecast {
    @Schema(description = "Time for the forecast data (ISO 8601).", example = "2025-06-16T13:00")
    private String time;

    @Schema(description = "Temperature in Celsius at the specified hour.", example = "22.1")
    private Double temperature;
}
