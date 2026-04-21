package com.smu.portlogisticsdistributionsystem.dto;

import lombok.Data;

@Data
public class LogisticQueryDTO {
    private Integer orderId;
    private Integer startPortId;
    private Integer endPortId;
    private Integer currentPortId;
    private Integer shipId;
    private Integer carId;
}