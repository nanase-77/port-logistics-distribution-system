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
     * 船舶ID
     */
    private Integer shipId;

    /**
     * 船舶载重（TEU）
     */
    private Integer shipCapacity;

    /**
     * 船舶当前已承载的TEU
     */
    private Integer shipCurrentTeu;

    /**
     * 使用的集装箱编号
     */
    private String containerCode;

    /**
     * 集装箱ID
     */
    private Integer containerId;

    /**
     * 集装箱容量（TEU）
     */
    private Integer containerCapacity;

    /**
     * 集装箱内的货物内容
     */
    private String containerContent;

    /**
     * 使用的车辆
     */
    private String carName;

    /**
     * 车辆ID
     */
    private Integer carId;

    /**
     * 车辆所属港口
     */
    private String carPort;

    /**
     * 货物名称
     */
    private String cargoName;

    /**
     * 货物体积
     */
    private Double volume;

    /**
     * TEU数量
     */
    private Integer teu;

    /**
     * 起始港口ID
     */
    private Long fromPortId;

    /**
     * 目的港口ID
     */
    private Long toPortId;

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