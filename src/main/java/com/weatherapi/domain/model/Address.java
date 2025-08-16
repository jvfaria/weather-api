package com.weatherapi.domain.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Address {
    @Schema(description = "City name.", example = "London")
    private String city;

    @Schema(description = "Town name.", example = "Cupertino")
    private String town;

    @Schema(description = "State district.", example = "Greater London")
    private String stateDistrict;

    @Schema(description = "State.", example = "England")
    private String state;

    @Schema(description = "Postcode.", example = "SW1A 2DU")
    private String postalcode;

    @Schema(description = "Country name.", example = "United Kingdom")
    private String country;

    @Schema(description = "Country code.", example = "gb")
    private String countryCode;
}
