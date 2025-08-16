package com.weatherapi.domain.dto.response;

import com.weatherapi.domain.model.Address;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO representing the response from the Nominatim geocoding API.
 * Used only internally to map provider data to application models.
 */
@Getter
@Setter
@Builder
public class GeocodeNominatimResponseDTO {
    @Schema(description = "Latitude of the geocoded location.", example = "-19.9279")
    private String lat;

    @Schema(description = "Longitude of the geocoded location.", example = "-44.1271")
    private String lon;

    @Schema(description = "Address details as returned by Nominatim.")
    private Address address;
}
