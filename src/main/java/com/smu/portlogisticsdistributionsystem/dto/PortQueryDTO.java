package com.smu.portlogisticsdistributionsystem.dto;

import lombok.Data;

@Data
public class PortQueryDTO {
    private String portName;
    private double longitude;
    private double latitude;
}
