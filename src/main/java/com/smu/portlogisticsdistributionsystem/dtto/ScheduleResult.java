package com.smu.portlogisticsdistributionsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 调度结果DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleResult {

    /**
     * 路径ID
     */
    private Long routeId;

    /**
     * 路径名称
     */
    private String routeName;

    /**
     * 总距离（公里）
     */
    private Double totalDistance;

    /**
     * 预计时间（小时）
     */
    private Double estimatedTime;

    /**
     * 途经港口列表
     */
    private List<PortNode> ports;

    /**
     * 使用的船舶
     */
    private String shipName;

    /**
     * 使用的车辆
     */
    private String carName;

    /**
     * 调度状态
     */
    private String status;

    /**
     * 港口节点信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PortNode {
        private Long portId;
        private String portName;
        private Double latitude;
        private Double longitude;
        private String arrivalTime;
        private String departureTime;
    }
}
