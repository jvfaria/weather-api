package com.weatherapi.domain.dto.request.thirdparty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO representing a forecast request, used to query weather data from the provider.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForecastRequestDTO {
    @Schema(description = "Zipcode information.", example = "32210110", requiredMode = Schema.RequiredMode.REQUIRED)
    private String zipcode;

    @Schema(description = "Latitude of the location.", example = "-19.9279", requiredMode = Schema.RequiredMode.REQUIRED)
    private String latitude;

    @Schema(description = "Longitude of the location.", example = "-44.1271", requiredMode = Schema.RequiredMode.REQUIRED)
    private String longitude;

    @Schema(description = "Start date for the forecast in yyyy-MM-dd format.", example = "2025-06-20", requiredMode = Schema.RequiredMode.REQUIRED)
    private String startDate;

    @Schema(description = "End date for the forecast in yyyy-MM-dd format.", example = "2025-06-22", requiredMode = Schema.RequiredMode.REQUIRED)
    private String endDate;

    @Schema(description = "Temperature unit", example = "CELSIUS")
    private String unit;
}
