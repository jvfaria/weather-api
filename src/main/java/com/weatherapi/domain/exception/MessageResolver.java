package com.weatherapi.domain.exception;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class MessageResolver {
    private static MessageSource messageSource;

    private MessageResolver() {}

    public static void setMessageSource(MessageSource source) {
        if (MessageResolver.messageSource == null) {
            MessageResolver.messageSource = source;
        }
    }

    public static String getMessage(String code, Object... args) {
        if (messageSource == null) {
            throw new IllegalStateException("MessageSource not initialized. Call MessageResolver.setMessageSource(...) during application startup.");
        }
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }
}
