package com.weatherapi.domain.dto.request.thirdparty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO representing a request to retrieve geolocation data based on a ZIP code.
 */
@Getter
@Setter
@AllArgsConstructor
public class GeocodeRequestDTO {
    @Schema(description = "ZIP code to geocode.", example = "32210110", requiredMode = Schema.RequiredMode.REQUIRED)
    private String zipcode;
}