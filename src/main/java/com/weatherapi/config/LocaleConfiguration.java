package com.weatherapi.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Locale;

@Configuration
@EnableConfigurationProperties(LocaleProperties.class)
@Slf4j
public class LocaleConfiguration {
    @Bean
    @ConditionalOnProperty(prefix = "weatherapi.i18n", name = "enabled", havingValue = "true")
    public LocaleResolver localeResolver(LocaleProperties localeProperties) {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        Locale locale = Locale.forLanguageTag(localeProperties.getLocale().replace("_", "-"));
        resolver.setDefaultLocale(locale);

        log.info("Application Locale manually set to: {}", locale);

        return resolver;
    }

    @Bean
    @ConditionalOnProperty(prefix = "weatherapi.i18n", name = "enabled", havingValue = "false", matchIfMissing = true)
    public LocaleResolver fallbackLocaleResolver() {
        // Use system default locale
        return new AcceptHeaderLocaleResolver();
    }

}
