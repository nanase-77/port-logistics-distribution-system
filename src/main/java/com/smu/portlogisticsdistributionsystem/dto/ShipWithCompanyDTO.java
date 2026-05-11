package com.smu.portlogisticsdistributionsystem.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShipWithCompanyDTO {
    private Integer id;
    private String shipName;
    private Integer companyId;
    private String companyName;
    private LocalDateTime updateTime;
    private LocalDateTime createTime;
}
