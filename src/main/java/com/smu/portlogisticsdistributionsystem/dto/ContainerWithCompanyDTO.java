package com.smu.portlogisticsdistributionsystem.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ContainerWithCompanyDTO {
    private Integer id;
    private String content;
    private Integer capacity;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}