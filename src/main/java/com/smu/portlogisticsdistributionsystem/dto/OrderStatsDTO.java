package com.smu.portlogisticsdistributionsystem.dto;

import lombok.Data;

@Data
public class OrderStatsDTO {
    private Integer pendingCount;
    private Integer progressCount;
    private Integer completedCount;
}
