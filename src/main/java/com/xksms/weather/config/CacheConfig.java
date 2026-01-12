package com.xksms.weather.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CacheConfig {
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("geoData");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                // 1. 允许缓存全国所有的区县文件
                .initialCapacity(1000)
                .maximumSize(3500)

                // 2. 策略：最后一次访问后 1 小时失效
                // 这样只有用户正在查看的区域会留在内存中
                .expireAfterAccess(Duration.ofHours(1))

                // 3. 关键配置：软引用
                // 如果某次你的 Java 业务逻辑突然需要大量内存（比如导出大 Excel）
                // JVM 发现内存紧张时，会自动回收这些 softValues 的缓存，避免 OOM
                .softValues()

                .recordStats());
        return cacheManager;
    }
}