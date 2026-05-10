package com.smu.portlogisticsdistributionsystem.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 地理服务配置类
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "geo")
public class GeoConfig {
    
    /**
     * EagleMap API Key
     */
    private String eagleMapApiKey = "YOUR_EAGLEMAP_API_KEY";
    
    /**
     * 基础费率（元/公里）
     */
    private double baseRate = 50.0;
    
    /**
     * 燃油附加费比例
     */
    private double fuelSurcharge = 0.15;
    
    /**
     * 港口服务费（元）
     */
    private double portServiceFee = 500.0;
    
    /**
     * 保险费率
     */
    private double insuranceRate = 0.02;
}
