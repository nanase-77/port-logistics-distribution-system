package com.smu.portlogisticsdistributionsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("cars")
public class Car {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    @TableField("car_name")
    private String carName;
    
    @TableField("image_url")
    private String imageUrl;
    
    @TableField("port_id")
    private Integer portId;
    
    @TableField("status")
    private String status;
    
    @TableField("update_time")
    private LocalDateTime updateTime;
    
    @TableField("create_time")
    private LocalDateTime createTime;
}