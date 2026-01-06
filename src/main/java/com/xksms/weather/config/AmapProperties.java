package com.xksms.weather.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "amap")
public class AmapProperties {
    /**
     * 高德 API Key
     */
    private String key;

    /**
     * 缓存过期时间（小时）
     */
    private int cacheTtlHours = 1;
}