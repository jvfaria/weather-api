package com.weatherapi.domain.dto.request;

import com.weatherapi.domain.enums.TemperatureUnitScaleEnum;
import com.weatherapi.domain.service.EnumValidator;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
public class WeatherApiRequestDTO {
    @Schema(
            description = "ZIP code for which to retrieve weather information.",
            example = "32210110",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "{error.zipcode.required}")
    private String zipcode;

    @Schema(
            description = "Start date (inclusive) in yyyy-MM-dd format.",
            example = "2025-06-16",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "{error.startDate.required}")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "{error.startDate.invalid}")
    private String startDate;

    @Schema(
            description = "End date (inclusive) in yyyy-MM-dd format.",
            example = "2025-06-18",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "{error.endDate.required}")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "{error.endDate.invalid}")
    private String endDate;

    @Schema(
            description = "Temperature unit",
            example = "CELSIUS"
    )
    @EnumValidator(enumClass = TemperatureUnitScaleEnum.class)
    private String unit;
}
