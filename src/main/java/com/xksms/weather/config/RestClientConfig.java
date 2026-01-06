package com.xksms.weather.config;

import com.xksms.weather.config.RestClientLoggingInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    /**
     * 定义一个通用的 RestClient.Builder
     * 这样你所有的 Service 都可以共用这套拦截器和超时配置
     */
    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder()
                .requestInterceptor(new RestClientLoggingInterceptor())
                // 这里可以统一配置连接池、超时等
                .requestFactory(new JdkClientHttpRequestFactory());
    }

    /**
     * 具体的和风天气客户端
     */
    @Bean
    public RestClient qWeatherClient(RestClient.Builder restClientBuilder) {
        return restClientBuilder.build();
    }
}