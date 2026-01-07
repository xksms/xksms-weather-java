package com.xksms.weather.controller;

import com.xksms.weather.service.GeoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/geo")
public class GeoController {

    @Autowired
    private GeoService geoService;

    @GetMapping(value = "/{adcode}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getDistrictData(@PathVariable String adcode) {
        return geoService.getGeoJson(adcode);
    }
}