package com.weatherapi.common;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.springframework.http.HttpStatus;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;

public class WiremockUtils {
    public static final String FAKE_ZIP_CODE = "95014";

    private WiremockUtils() {
    }

    public static final String GEOCODE_EXTENAL_FAKE_API = "/v1/geocode/search";
    public static final String FORECAST_EXTENAL_FAKE_API = "/v1/forecast";
    public static final String WEATHER_FAKE_API = "/v1/api/weather";

    public static void geocodeFakeApiStubSuccess() {
        stubFor(WireMock.get(urlPathEqualTo(GEOCODE_EXTENAL_FAKE_API))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                                                [
                                    {
                                        "lat": "37.7749",
                                        "lon": "-122.4194",
                                        "address": {
                                          "city": "Cupertino",
                                          "state_district": "Santa Clara County",
                                          "state": "California",
                                          "postcode": 95014,
                                          "country": "USA"
                                        }
                                    }
                                ]
                                """)));
    }

    public static void geocodeFakeApiStubFail() {
        stubFor(WireMock.get(urlPathEqualTo(GEOCODE_EXTENAL_FAKE_API))
                .willReturn(aResponse().withStatus(502)));
    }

    public static void geocodeFakeApiStubInvalidResponse() {
        stubFor(WireMock.get(urlPathMatching("/v1/forecast"))
                .willReturn(aResponse().withStatus(502)));
    }

    public static void forecastFakeApiStubSuccess() {
        stubFor(WireMock.get(urlPathEqualTo(FORECAST_EXTENAL_FAKE_API))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                    "timezone": "America/Los_Angeles",
                                    "timezone_abbreviation": "GMT-7",
                                    "hourly_units": {
                                        "time": "iso8601",
                                        "temperature_2m": "celsius"
                                    },
                                    "hourly": {
                                        "time": ["2025-06-20T00:00", "2025-06-20T01:00"],
                                        "temperature_2m": [16.1, 16.0]
                                    }
                                }
                                """)));
    }


}
