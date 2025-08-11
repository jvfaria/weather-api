package com.weatherapi.unit;

import com.weatherapi.common.AbstractBaseTestUnit;
import com.weatherapi.common.WiremockUtils;
import com.weatherapi.domain.dto.request.WeatherApiRequestDTO;
import com.weatherapi.domain.enums.TemperatureUnitScaleEnum;
import com.weatherapi.domain.exception.validation.WeatherGetValidationException;
import com.weatherapi.domain.service.WeatherRequestValidatorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ValidatorServiceTest extends AbstractBaseTestUnit {
    public static final String START_DATE = "2025-06-10";
    public static final String END_DATE = "2025-06-16";
    private final WeatherRequestValidatorService validator = new WeatherRequestValidatorService();

    @Test
    @DisplayName("Should not throw error when dates are valid")
    void shouldNotThrowExceptionWhenDatesAreValid() {
        WeatherApiRequestDTO request = buildRequest(START_DATE, END_DATE);
        assertDoesNotThrow(() -> validator.validateBusinessRules(request));
    }

    @Test
    @DisplayName("Should throw error when start date is invalid")
    void shouldThrowExceptionWhenStartDateIsInvalid() {
        WeatherApiRequestDTO request = buildRequest("2025/10/10", END_DATE);
        assertThrows(WeatherGetValidationException.class, () -> validator.validateBusinessRules(request));
    }

    @Test
    @DisplayName("Should throw error when end date range is invalid")
    void shouldThrowExceptionWhenEndDateIsInvalid() {
        WeatherApiRequestDTO request = buildRequest(START_DATE, "2025/10/10");
        assertThrows(WeatherGetValidationException.class, () -> validator.validateBusinessRules(request));
    }

    @Test
    @DisplayName("Should throw error when end date is invalid exceeding 7 days")
    void shouldThrowExceptionWhenEndDateExceeds7DaysInvalidRange() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate = LocalDate.parse(START_DATE, formatter).plusDays(8);

        WeatherApiRequestDTO request = buildRequest(START_DATE, endDate.format(formatter));
        assertThrows(WeatherGetValidationException.class, () -> validator.validateBusinessRules(request));
    }

    @Test
    @DisplayName("Should throw error when end date range is invalid start date is after end date")
    void shouldThrowExceptionWhenDateRangeIsInvalid() {
        WeatherApiRequestDTO request = buildRequest(END_DATE, START_DATE);
        assertThrows(WeatherGetValidationException.class, () -> validator.validateBusinessRules(request));
    }


    private WeatherApiRequestDTO buildRequest(String startDate, String endDate) {
        return WeatherApiRequestDTO.builder()
                .zipcode(WiremockUtils.FAKE_ZIP_CODE)
                .unit(TemperatureUnitScaleEnum.CELSIUS.getName())
                .startDate(startDate)
                .endDate(endDate).build();
    }
}
