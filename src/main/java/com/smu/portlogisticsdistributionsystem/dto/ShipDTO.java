package com.smu.portlogisticsdistributionsystem.dto;

import lombok.Data;

@Data
public class ShipDTO {
    private Integer id;
    private String shipName;
    private Integer companyId;
    private Integer status;
    private Double capacity;
    private Integer currentPortId;
}