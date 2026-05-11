package com.smu.portlogisticsdistributionsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 调度请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchedulingRequest {

    /**
     * 货物名称
     */
    private String cargoName;

    /**
     * 货物重量（吨）
     */
    private Double weight;

    /**
     * 起始港口ID
     */
    private Long fromPortId;

    /**
     * 目标港口ID
     */
    private Long toPortId;

    /**
     * 优先级（HIGH/MEDIUM/LOW）
     */
    private String priority;

    /**
     * 期望到达时间
     */
    private String expectedArrivalTime;

    /**
     * 是否需要车辆运输
     */
    private Boolean needCarTransport;
}
