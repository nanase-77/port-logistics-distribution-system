package com.smu.portlogisticsdistributionsystem.dto;

import lombok.Data;

@Data
public class CarDTO {
    private Integer id;
    private String carName;
    private String imageUrl;
    private Integer portId;
    private String status;
}