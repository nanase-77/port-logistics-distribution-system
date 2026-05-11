package com.smu.portlogisticsdistributionsystem.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CarWithPortDTO {
    private Integer id;
    @TableField("car_name")
    private String carName;
    @TableField("image_url")
    private String imageUrl;
    @TableField("port_id")
    private Integer portId;
    @TableField("port_name")
    private String portName;
    private String status;
    @TableField("update_time")
    private LocalDateTime updateTime;
    @TableField("create_time")
    private LocalDateTime createTime;
}
