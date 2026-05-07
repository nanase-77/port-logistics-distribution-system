package com.smu.portlogisticsdistributionsystem.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CarVO {
    private Integer id;
    private String carName;
    private String imageUrl;
    private Integer portId;
    private String status;
    private LocalDateTime updateTime;
    private LocalDateTime createTime;

    private String portName;
}