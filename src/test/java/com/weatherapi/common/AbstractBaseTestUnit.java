package com.weatherapi.common;

import com.weatherapi.domain.exception.MessageResolver;
import org.junit.jupiter.api.BeforeAll;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.context.MessageSource;

import java.util.Locale;

public abstract class AbstractBaseTestUnit {
    @Mock
    public static MessageSource mockMessageSource;

    @BeforeAll
    static void initMessageSource() {
        Mockito.when(mockMessageSource.getMessage(Mockito.anyString(), Mockito.any(), Mockito.any(Locale.class)))
                .thenAnswer(invocation -> {
                    String code = invocation.getArgument(0);
                    Object[] args = invocation.getArgument(1);
                    return args != null && args.length > 0
                            ? code + " - " + String.join(", ", toStringArray(args))
                            : code;
                });

        MessageResolver.setMessageSource(mockMessageSource);
    }

    private static String[] toStringArray(Object[] objects) {
        String[] strings = new String[objects.length];
        for (int i = 0; i < objects.length; i++) {
            strings[i] = String.valueOf(objects[i]);
        }
        return strings;
    }
}
