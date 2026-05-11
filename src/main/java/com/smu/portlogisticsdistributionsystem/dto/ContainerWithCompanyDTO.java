package com.smu.portlogisticsdistributionsystem.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ContainerWithCompanyDTO {
    private Integer id;
    private String content;
    private String size;
    @TableField("company_id")
    private Integer companyId;
    @TableField("company_name")
    private String companyName;
    @TableField("update_time")
    private LocalDateTime updateTime;
    @TableField("create_time")
    private LocalDateTime createTime;
    @TableField("state")
    private Integer state;
    
    public String getStateText() {
        if (state == null) {
            return "未知";
        }
        return switch (state) {
            case 0 -> "空闲";
            case 1 -> "使用中";
            case 2 -> "运输中";
            default -> "未知";
        };
    }
}
