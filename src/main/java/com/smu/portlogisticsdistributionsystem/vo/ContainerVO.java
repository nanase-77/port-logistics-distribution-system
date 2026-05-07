package com.smu.portlogisticsdistributionsystem.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ContainerVO {
    private Integer id;
    private String content;
    private String size;
    private Integer companyId;
    private LocalDateTime updateTime;
    private LocalDateTime createTime;

    private String companyName;
}