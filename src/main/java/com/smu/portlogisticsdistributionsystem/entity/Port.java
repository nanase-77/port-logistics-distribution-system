package com.smu.portlogisticsdistributionsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("ports")
public class Port {
    @TableId(type = IdType.AUTO)
    private Integer id;
    @TableField("port_name")
    private String portName;
    private double longitude;
    private double latitude;
    @TableField("update_time")
    private LocalDateTime updateTime;
    @TableField("create_time")
    private LocalDateTime createTime;
}
