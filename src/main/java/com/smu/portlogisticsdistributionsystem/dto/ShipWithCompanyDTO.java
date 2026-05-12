package com.smu.portlogisticsdistributionsystem.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShipWithCompanyDTO {
    private Integer id;
    private String shipName;
    private Integer companyId;
    private String companyName;
    private Integer status;
    private Double capacity;
    private Integer currentTeu;
    @TableField("current_port_id")
    private Integer currentPortId;
    private LocalDateTime updateTime;
    private LocalDateTime createTime;
}