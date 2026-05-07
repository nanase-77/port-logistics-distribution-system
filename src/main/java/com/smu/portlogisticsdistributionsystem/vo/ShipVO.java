package com.smu.portlogisticsdistributionsystem.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShipVO {
    private Integer id;
    private String shipName;
    private String companyId;
    private LocalDateTime updateTime;
    private LocalDateTime createTime;

    private String companyName;
}
