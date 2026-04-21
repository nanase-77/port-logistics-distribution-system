package com.smu.portlogisticsdistributionsystem.dto;

import lombok.Data;

@Data
public class CarQueryDTO {
    private String carName;
    private Integer portId;
    private String status;
}