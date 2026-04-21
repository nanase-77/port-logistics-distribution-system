package com.smu.portlogisticsdistributionsystem.dto;

import lombok.Data;

@Data
public class ContainerQueryDTO {
    private String content;
    private String size;
    private Integer companyId;
}