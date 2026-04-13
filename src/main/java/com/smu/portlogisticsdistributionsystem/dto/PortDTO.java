package com.smu.portlogisticsdistributionsystem.dto;

import lombok.Data;

@Data
public class PortDTO {
    private Integer id;
    private String portName;
    private double longitude;
    private double latitude;
}
