package com.weatherapi.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "Weather response including geocoding details and weather data.")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GeocodeResponse {
    @Schema(description = "Latitude of the location.", example = "51.5073219")
    private String lat;

    @Schema(description = "Longitude of the location.", example = "-0.1276474")
    private String lon;

    @Schema(description = "Display name is the full address detail when there isn't address detail info from API.", example = "323123-123, Rua 2, Sao Paulo, Brasil")
    private String rawDisplayName;

    @Schema(description = "Address details for the location.")
    private Address address;
}

