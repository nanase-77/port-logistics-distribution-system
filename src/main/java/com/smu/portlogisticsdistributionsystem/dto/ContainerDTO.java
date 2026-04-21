package com.smu.portlogisticsdistributionsystem.dto;

import lombok.Data;

@Data
public class ContainerDTO {
    private Integer id;
    private String content;
    private String size;
    private Integer companyId;
}