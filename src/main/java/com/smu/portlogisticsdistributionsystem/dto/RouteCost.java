package com.smu.portlogisticsdistributionsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 航线费用详情
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteCost {

    /**
     * 距离（公里）
     */
    private Double distance;

    /**
     * 货物重量（吨）
     */
    private Double weight;

    /**
     * 基础运费（元）
     */
    private Double basicFreight;

    /**
     * 燃油附加费（元）
     */
    private Double fuelSurcharge;

    /**
     * 港口服务费（元）
     */
    private Double portFee;

    /**
     * 保险费（元）
     */
    private Double insurance;

    /**
     * 其他费用（元）
     */
    private Double otherFees;

    /**
     * 总费用（元）
     */
    private Double total;

    /**
     * 预计时间（小时）
     */
    private Double estimatedTime;
}
