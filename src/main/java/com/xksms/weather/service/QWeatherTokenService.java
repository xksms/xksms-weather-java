package com.xksms.weather.service;

import com.xksms.weather.config.QWeatherProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.OffsetDateTime;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class QWeatherTokenService {

    private final QWeatherProperties properties;

    // 缓存私钥对象（解析私钥很慢）
    private PrivateKey cachedPrivateKey;

    // 缓存 Token 字符串
    private volatile String cachedToken;
    // 缓存 Token 的过期时间戳（秒）
    private volatile long tokenExpiryTime = 86400L;

    /**
     * 获取 Token（带缓存逻辑）
     */
    public String getToken() {
        long now = OffsetDateTime.now().toEpochSecond();

        // 1. 检查缓存是否存在且未过期（预留 60 秒缓冲区，防止临界点失效）
        if (cachedToken != null && now < (tokenExpiryTime - 60)) {
            return cachedToken;
        }

        // 2. 加锁重新生成
        synchronized (this) {
            // 双重检查，防止并发时多次生成
            now = OffsetDateTime.now().toEpochSecond();
            if (cachedToken != null && now < (tokenExpiryTime - 60)) {
                return cachedToken;
            }

            log.info(">>> 缓存失效或即将过期，重新生成 QWeather JWT Token");
            return generateNewToken(now);
        }
    }

    private String generateNewToken(long now) {
        try {
            PrivateKey privateKey = getPrivateKey();

            // Header
            String headerJson = String.format("{\"alg\":\"EdDSA\",\"kid\":\"%s\"}", properties.getKId());
            String headerEncoded = base64UrlEncode(headerJson.getBytes(StandardCharsets.UTF_8));

            // Payload: 设置24小时有效期 (900秒)
            long iat = now - 30; // 稍微提前一点防止服务器时间差
            long exp = iat + 86400;
            String payloadJson = String.format("{\"sub\":\"%s\",\"iat\":%d,\"exp\":%d}", properties.getProjectId(), iat, exp);
            String payloadEncoded = base64UrlEncode(payloadJson.getBytes(StandardCharsets.UTF_8));

            // Signature
            String dataToSign = headerEncoded + "." + payloadEncoded;
            Signature signer = Signature.getInstance("EdDSA");
            signer.initSign(privateKey);
            signer.update(dataToSign.getBytes(StandardCharsets.UTF_8));
            String signatureEncoded = base64UrlEncode(signer.sign());

            // 更新缓存
            this.cachedToken = dataToSign + "." + signatureEncoded;
            this.tokenExpiryTime = exp;

            return this.cachedToken;
        } catch (Exception e) {
            log.error("JWT Token 生成失败", e);
            throw new RuntimeException("Token generation failed", e);
        }
    }

    private PrivateKey getPrivateKey() throws Exception {
        if (cachedPrivateKey != null) return cachedPrivateKey;

        // 解析逻辑保持不变...
        String privateKeyString  = properties.getPrivateKey()
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");
        byte[] keyBytes = Base64.getDecoder().decode(privateKeyString );
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("EdDSA");
        this.cachedPrivateKey = kf.generatePrivate(spec);
        return cachedPrivateKey;
    }

    private String base64UrlEncode(byte[] input) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(input);
    }
}