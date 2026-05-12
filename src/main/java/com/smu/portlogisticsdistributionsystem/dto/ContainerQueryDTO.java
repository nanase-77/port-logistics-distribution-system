package com.smu.portlogisticsdistributionsystem.dto;

import lombok.Data;

@Data
public class ContainerQueryDTO {
    private String content;
    private Integer capacity;
    private Integer status;
}