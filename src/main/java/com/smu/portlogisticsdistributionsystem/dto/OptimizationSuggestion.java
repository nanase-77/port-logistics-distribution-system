package com.smu.portlogisticsdistributionsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 优化建议DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptimizationSuggestion {

    /**
     * 建议ID
     */
    private Long suggestionId;

    /**
     * 建议类型（资源利用率/路径优化/负载均衡）
     */
    private String type;

    /**
     * 建议标题
     */
    private String title;

    /**
     * 建议详情
     */
    private String description;

    /**
     * 优化收益（百分比）
     */
    private Double expectedBenefit;

    /**
     * 涉及资源ID
     */
    private Long resourceId;

    /**
     * 资源名称
     */
    private String resourceName;
}
