package com.smu.portlogisticsdistributionsystem.dto;

import lombok.Data;

@Data
public class OrderQueryDTO {
    private String orderNumber;
    private Integer userId;
    private String status;
}