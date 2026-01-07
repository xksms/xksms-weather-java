package com.xksms.weather.service.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.xksms.weather.config.QWeatherProperties;
import com.xksms.weather.service.CityLookupService;
import com.xksms.weather.service.QWeatherTokenService;
import com.xksms.weather.service.WeatherService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class WeatherServiceImpl implements WeatherService {

	@Resource
	private QWeatherProperties qWeatherProperties;

	@Resource
	private QWeatherTokenService qWeatherTokenService;

	@Resource
	private CityLookupService cityLookupService;

	@Resource(name = "qWeatherClient")
	private  RestClient weatherClient;

	// 缓存配置：1小时过期，最大存储500条
	private final Cache<String, String> cache = Caffeine.newBuilder()
			.expireAfterWrite(1, TimeUnit.HOURS)
			.maximumSize(500)
			.build();


	@Override
	public String getWeather(String adcode) {
		return cache.get(adcode, this::fetchWeatherFromAmap);
	}

	private String fetchWeatherFromAmap(String adcode) {
		// 动态生成 Token
		String jwtToken = qWeatherTokenService.getToken();

		// 1. 本地 O(1) 复杂度直接拿到和风天气的 LocationID
		String locationId = cityLookupService.getLocationId(adcode);
		//解析body获取LocationID
		log.info(">>> 真正调用合肥天气 API，根据adcode调用获取LocationID: {}", adcode);
		return weatherClient.get()
				.uri("https://"+qWeatherProperties.getApiHost()+"/v7/weather/now?location={LocationID}",101010100)
				// 2. 核心修正：header 名和值要分开传
				.header("Authorization", "Bearer " + jwtToken)
				// 3. 建议加上：明确告诉服务器我们要 gzip，虽然 RestClient 默认支持，但显式声明更稳
				.header("Accept-Encoding", "gzip")
				.retrieve()
				.body(String.class);
	}
}
