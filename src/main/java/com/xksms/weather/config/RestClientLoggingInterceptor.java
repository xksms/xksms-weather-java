package com.xksms.weather.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class RestClientLoggingInterceptor implements ClientHttpRequestInterceptor {
    private static final Logger log = LoggerFactory.getLogger(RestClientLoggingInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        // 记录请求
        log.debug(">>> [HTTP Request] Method: {}, URI: {}", request.getMethod(), request.getURI());
        log.debug(">>> [HTTP Headers] {}", request.getHeaders());
        if (body.length > 0) {
            log.debug(">>> [HTTP Body] {}", new String(body, StandardCharsets.UTF_8));
        }

        ClientHttpResponse response = execution.execute(request, body);

        // 记录响应
        log.debug("<<< [HTTP Response] Status: {}", response.getStatusCode());
        return response;
    }
}