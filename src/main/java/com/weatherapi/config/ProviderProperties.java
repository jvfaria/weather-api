package com.weatherapi.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "weatherapi")
public class ProviderProperties {
    private Provider provider = new Provider();

    @Data
    public static class Provider {
        private Geocoding geocoding = new Geocoding();
        private Forecast forecast = new Forecast();

        @Data
        public static class Geocoding {
            private String defaultProvider;
            private Nominatim nominatim = new Nominatim();
            // Here could be added future providers

            @Data
            public static class Nominatim {
                private String baseUrl;
            }
        }

        @Data
        public static class Forecast {
            private String defaultProvider;
            private OpenMeteo meteo = new OpenMeteo();
            // Here could be added future providers

            @Data
            public static class OpenMeteo {
                private String baseUrl;
            }
        }
    }




}
