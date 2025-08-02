package com.weatherapi.config;

import com.weatherapi.domain.exception.MessageResolver;
import jakarta.annotation.PostConstruct;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageResolverConfiguration {
    private final MessageSource messageSource;

    public MessageResolverConfiguration(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @PostConstruct
    public void init() {
        MessageResolver.setMessageSource(messageSource);
    }
}
