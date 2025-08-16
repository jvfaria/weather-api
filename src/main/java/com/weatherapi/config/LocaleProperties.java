package com.weatherapi.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "weatherapi.i18n")
public class LocaleProperties {
    private boolean enabled = false;
    private String locale = "en_US";
}
