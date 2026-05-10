package com.smu.portlogisticsdistributionsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 替代路径DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlternativeRoute {

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
     * 与原路径的差异描述
     */
    private String difference;

    /**
     * 途经港口列表
     */
    private List<ScheduleResult.PortNode> ports;

    /**
     * 推荐优先级（1-5，5最高）
     */
    private Integer priority;
}
