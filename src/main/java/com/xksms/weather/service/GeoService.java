package com.xksms.weather.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class GeoService {
    // 定义文件存储路径（存放在项目根目录下的 data/geo 文件夹中）
    private static final String STORAGE_PATH = "data/geo/";
    private final RestTemplate restTemplate = new RestTemplate();

    public String getGeoJson(String adcode) {
        String fileName = adcode + "_full.json";
        String fileName2 = adcode + ".json";
        Path filePath = Paths.get(STORAGE_PATH, fileName);

        // 1. 检查本地是否存在文件
        if (Files.exists(filePath)) {
            try {
                return Files.readString(filePath);
            } catch (IOException e) {
                System.err.println("读取本地缓存失败: " + e.getMessage());
            }
        }

        // 2. 本地不存在，调用阿里 API
        String url = "https://geo.datav.aliyun.com/areas_v3/bound/" + fileName;
        try {
            String data = restTemplate.getForObject(url, String.class);
            if (data != null) {
                // 3. 异步或同步保存到本地，防止下次丢失
                saveToFile(filePath, data);
                return data;
            }
        } catch (Exception e) {
            //第一次失败，尝试第二个文件名
            try {
                String url2 = "https://geo.datav.aliyun.com/areas_v3/bound/" + fileName2;
                String data2 = restTemplate.getForObject(url2, String.class);
                if (data2 != null) {
                    saveToFile(Paths.get(STORAGE_PATH, fileName2), data2);
                    return data2;
                }
            } catch (Exception ex) {
                System.err.println("从外部抓取数据失败: " + ex.getMessage());
            }
        }

        return null; 
    }

    private void saveToFile(Path path, String content) {
        try {
            Files.createDirectories(path.getParent()); // 确保文件夹存在
            Files.writeString(path, content);
            System.out.println("成功持久化地理数据: " + path.getFileName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}