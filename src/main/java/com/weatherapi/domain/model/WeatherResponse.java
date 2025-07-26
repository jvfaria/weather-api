package com.weatherapi.domain.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Schema(description = "Weather final response including geocoding details and weather data.")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WeatherResponse {
    @Schema(
            description = "City of the postal code location.",
            example = "Cupertino"
    )
    private String city;

    @Schema(
            description = "Latitude of the location.",
            example = "51.5073219"
    )
    private String latitude;

    @Schema(
            description = "Longitude of the location.",
            example = "-0.1276474"
    )
    private String longitude;

    @Schema(
            description = "Full display name of the location.",
            example = "Cupertino, California, USA, 95014"
    )
    private String displayName;

    @Schema(
            description = "Current temperature in Celsius or Fahrenheit.",
            example = "18.5"
    )
    private Double currentTemperature;
    @Schema(
            description = "Shows if temperature is in Celsius or Fahrenheit.",
            example = "°C"
    )
    private String unit;

    @Schema(
            description = "Highest temperature for the day(s) in Celsius or Fahreinheit.",
            example = "21.3"
    )
    private Double high;

    @Schema(
            description = "Lowest temperature for the day(s) in Celsius or Fahreinheit.",
            example = "12.1"
    )
    private Double low;

    @Schema(
            description = "Display the timezone of the city postal code.",
            example = "America/Sao_paulo"
    )
    private String timezone;

    @Schema(
            description = "Display the timezone unit of the city postal code.",
            example = "GMT-3"
    )
    private String offset;

    @Schema(
            description = "Indicates if this response was served from cache.",
            example = "false"
    )
    private Boolean isCached;

    @Schema(
            description = "Address details for the location."
    )
    private Address address;

    @Schema(
            description = "Hourly forecast for the day or range."
    )
    private List<HourlyForecast> hourly;
}
