package com.weatherapi.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class LoggingRequestInterceptor implements ClientHttpRequestInterceptor {
    private static final Logger log = LoggerFactory.getLogger(LoggingRequestInterceptor.class);

    @Override
    public ClientHttpResponse intercept(
            HttpRequest request,
            byte[] body,
            ClientHttpRequestExecution execution) throws IOException {

        logRequest(request, body);

        long start = System.currentTimeMillis();

        ClientHttpResponse response = execution.execute(request, body);

        long end = System.currentTimeMillis();
        long duration = end - start;

        logResponse(response, duration);

        return response;
    }

    private void logRequest(HttpRequest request, byte[] body) {
        log.info("=== [HTTP REQUEST] ===");
        log.info("URI         : {}", request.getURI());
        log.info("Method      : {}", request.getMethod());
        log.info("Headers     : {}", request.getHeaders());
        log.info("Body        : {}", new String(body));
    }

    private void logResponse(ClientHttpResponse response, long duration) throws IOException {
        log.info("=== [HTTP RESPONSE] ===");
        log.info("Status code  : {}", response.getStatusCode());
        log.info("Status text  : {}", response.getStatusText());
        log.info("Headers      : {}", response.getHeaders());
        log.info("Duration     : {} ms", duration);
    }
}
