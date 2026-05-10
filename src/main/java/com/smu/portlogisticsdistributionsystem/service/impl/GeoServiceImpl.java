package com.smu.portlogisticsdistributionsystem.service.impl;

import com.smu.portlogisticsdistributionsystem.config.GeoConfig;
import com.smu.portlogisticsdistributionsystem.dto.RouteCost;
import com.smu.portlogisticsdistributionsystem.service.GeoService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * 地理服务实现类
 * 集成高德地图 EagleMap API 进行距离计算
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GeoServiceImpl implements GeoService {

    private final GeoConfig geoConfig;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final double EARTH_RADIUS = 6371.0;
    private static final double DEFAULT_SPEED = 101.9; // 55节转换为公里/小时

    /**
     * 使用高德地图 EagleMap API 计算两点间距离
     */
    @Override
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // 优先使用高德地图 API
        Double apiDistance = calculateDistanceByEagleMap(lat1, lon1, lat2, lon2);
        if (apiDistance != null) {
            return apiDistance;
        }
        
        // 备用方案：使用 Haversine 公式计算球面距离
        return calculateHaversineDistance(lat1, lon1, lat2, lon2);
    }

    /**
     * 调用高德地图 EagleMap API 计算距离
     */
    private Double calculateDistanceByEagleMap(double lat1, double lon1, double lat2, double lon2) {
        String apiKey = geoConfig.getEagleMapApiKey();
        if (apiKey == null || apiKey.isEmpty()) {
            log.debug("EagleMap API key not configured, falling back to Haversine");
            return null;
        }

        try {
            // 高德地图路径规划 API
            String url = "https://restapi.amap.com/v3/direction/driving?" +
                    "origin=" + lon1 + "," + lat1 +
                    "&destination=" + lon2 + "," + lat2 +
                    "&key=" + apiKey;

            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            
            String status = root.get("status").asText();
            if ("1".equals(status)) {
                JsonNode route = root.get("route");
                if (route != null) {
                    JsonNode paths = route.get("paths");
                    if (paths != null && paths.isArray() && paths.size() > 0) {
                        double distance = paths.get(0).get("distance").asDouble() / 1000; // 米转公里
                        log.debug("EagleMap API distance: {} km", distance);
                        return Math.round(distance * 100.0) / 100.0;
                    }
                }
            } else {
                String info = root.has("info") ? root.get("info").asText() : "Unknown error";
                log.debug("EagleMap API error: {}", info);
            }
        } catch (Exception e) {
            log.debug("EagleMap API call failed: {}", e.getMessage());
        }
        
        return null;
    }

    /**
     * 使用 Haversine 公式计算球面距离
     */
    private double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distance = EARTH_RADIUS * c;
        log.debug("Haversine distance: {} km", distance);
        return Math.round(distance * 100.0) / 100.0;
    }

    @Override
    public RouteCost calculateCost(double distance, double weight) {
        double baseRate = geoConfig.getBaseRate();
        double weightFactor = Math.max(1.0, weight / 10.0);
        double basicFreight = distance * baseRate * weightFactor;

        double fuelSurcharge = basicFreight * geoConfig.getFuelSurcharge();
        double portFee = geoConfig.getPortServiceFee() * 2;
        double insurance = (basicFreight + fuelSurcharge) * geoConfig.getInsuranceRate();
        double otherFees = 200.0;

        double total = basicFreight + fuelSurcharge + portFee + insurance + otherFees;

        return RouteCost.builder()
                .distance(Math.round(distance * 100.0) / 100.0)
                .weight(Math.round(weight * 100.0) / 100.0)
                .basicFreight(Math.round(basicFreight * 100.0) / 100.0)
                .fuelSurcharge(Math.round(fuelSurcharge * 100.0) / 100.0)
                .portFee(Math.round(portFee * 100.0) / 100.0)
                .insurance(Math.round(insurance * 100.0) / 100.0)
                .otherFees(Math.round(otherFees * 100.0) / 100.0)
                .total(Math.round(total * 100.0) / 100.0)
                .build();
    }

    @Override
    public double calculateTime(double distance, double speed) {
        double actualSpeed = speed > 0 ? speed : DEFAULT_SPEED;
        double time = distance / actualSpeed;
        log.debug("Calculated time: {} hours", time);
        return Math.round(time * 100.0) / 100.0;
    }

    @Override
    public RouteCost calculateRoute(double lat1, double lon1, double lat2, double lon2, double weight) {
        double distance = calculateDistance(lat1, lon1, lat2, lon2);
        RouteCost cost = calculateCost(distance, weight);
        cost.setEstimatedTime(calculateTime(distance, DEFAULT_SPEED));
        return cost;
    }

    /**
     * 获取高德地图静态地图URL
     */
    public String getStaticMapUrl(double lat, double lon, int zoom, int width, int height) {
        String apiKey = geoConfig.getEagleMapApiKey();
        if (apiKey == null || apiKey.isEmpty()) {
            return null;
        }
        
        return String.format("https://restapi.amap.com/v3/staticmap?" +
                "center=%f,%f&zoom=%d&size=%dx%d&markers=mid,0xFF0000,A:%f,%f&key=%s",
                lon, lat, zoom, width, height, lon, lat, apiKey);
    }
}
