package com.smu.portlogisticsdistributionsystem.service;

import com.smu.portlogisticsdistributionsystem.dto.RouteCost;

/**
 * 地理服务接口
 * 提供距离计算和费用估算功能
 */
public interface GeoService {

    /**
     * 计算两个坐标之间的直线距离（Haversine公式）
     * @param lat1 起点纬度
     * @param lon1 起点经度
     * @param lat2 终点纬度
     * @param lon2 终点经度
     * @return 距离（公里）
     */
    double calculateDistance(double lat1, double lon1, double lat2, double lon2);

    /**
     * 计算航线费用
     * @param distance 距离（公里）
     * @param weight 货物重量（吨）
     * @return 费用详情
     */
    RouteCost calculateCost(double distance, double weight);

    /**
     * 获取运输时间估算
     * @param distance 距离（公里）
     * @param speed 平均速度（公里/小时），默认55节（约101.9公里/小时）
     * @return 时间（小时）
     */
    double calculateTime(double distance, double speed);

    /**
     * 计算完整的航线信息（距离、时间、费用）
     * @param lat1 起点纬度
     * @param lon1 起点经度
     * @param lat2 终点纬度
     * @param lon2 终点经度
     * @param weight 货物重量（吨）
     * @return 航线费用详情
     */
    RouteCost calculateRoute(double lat1, double lon1, double lat2, double lon2, double weight);
}
