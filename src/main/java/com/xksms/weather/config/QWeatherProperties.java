package com.xksms.weather.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "qweather")
public class QWeatherProperties {

    /**
     * 和风天气 API Key
     */
    private String privateKey;


    /**
     *和风天气 API host
     */
    private String apiHost;

    private String kId;    // 对应 kid
    private String projectId;    // 对应 项目id

    /**
     * 缓存过期时间（小时）
     */
    private int cacheTtlHours = 1;
}