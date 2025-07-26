package com.weatherapi.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Forecast response with summary and detailed hourly forecast for the location.")
public class ForecastResponse {
    @Schema(description = "Latitude of the forecast location.", example = "-19.9279")
    private double latitude;

    @Schema(description = "Longitude of the forecast location.", example = "-44.1271")
    private double longitude;

    @Schema(description = "Timezone of the city forecast.", example = "America/Sao_paulo")
    private String timezone;

    @Schema(description = "Timezone of the city forecast.", example = "GMT-3")
    private String offset;

    @Schema(description = "Current temperature at the requested location in Celsius.", example = "23.5")
    private Double currentTemperature;

    @Schema(description = "Temperature unit in Celsius or Fahrenhit.", example = "°C")
    private String unit;

    @Schema(description = "Highest temperature in the forecast period.", example = "27.8")
    private Double high;

    @Schema(description = "Lowest temperature in the forecast period.", example = "15.2")
    private Double low;

    @Schema(description = "Hourly forecast data.")
    private List<HourlyForecast> hourly;

    @Schema(description = "Indicates if this response was served from cache.", example = "false")
    private Boolean isCached;
}
