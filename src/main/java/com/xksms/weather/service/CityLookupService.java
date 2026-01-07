package com.xksms.weather.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class CityLookupService {

    // Key: adcode (110000), Value: locationID (101010100)
    private final Map<String, String> adcodeMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        try {
            log.info(">>> 开始加载本地城市列表数据...");
            ClassPathResource resource = new ClassPathResource("file/China-City-List-latest.csv");
            try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                // 跳过前两行（版本信息和表头）
                br.readLine(); 
                br.readLine();
                
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 14) {
                        String locationId = parts[0].trim();
                        String adCode = parts[13].trim();
                        if (!adCode.isEmpty()) {
                            adcodeMap.put(adCode, locationId);
                        }
                    }
                }
            }
            log.info(">>> 城市数据加载完成，共索引 {} 条记录", adcodeMap.size());
        } catch (Exception e) {
            log.error(">>> 加载城市列表失败", e);
        }
    }

    public String getLocationId(String adCode) {
        // 如果找不到，默认返回北京 101010100 或抛出异常
        return adcodeMap.getOrDefault(adCode, "101010100");
    }
}