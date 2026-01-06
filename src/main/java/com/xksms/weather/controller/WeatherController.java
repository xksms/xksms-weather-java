package com.xksms.weather.controller;


import com.xksms.weather.service.WeatherService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/weather")
@CrossOrigin(origins = "*") // 允许前端地图跨域请求
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/{adcode}")
    public String getWeather(@PathVariable String adcode) {
        return weatherService.getWeather(adcode);
    }
}