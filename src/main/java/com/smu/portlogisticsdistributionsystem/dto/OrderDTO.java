package com.smu.portlogisticsdistributionsystem.dto;

import lombok.Data;

@Data
public class OrderDTO {
    private Integer id;
    private String orderNumber;
    private Integer userId;
    private String status;
    private String containerIds;
}