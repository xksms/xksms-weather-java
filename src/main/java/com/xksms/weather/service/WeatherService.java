package com.xksms.weather.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.util.concurrent.TimeUnit;

@Service
public interface WeatherService {



    String getWeather(String adcode);

}