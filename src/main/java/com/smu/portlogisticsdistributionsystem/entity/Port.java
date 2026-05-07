package com.smu.portlogisticsdistributionsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@TableName("ports")
public class Port {
    @TableId(type= IdType.AUTO)
    private Integer id;
    private String portName;
    private double longitude;
    private double latitude;
    private LocalDateTime updateTime;
    private LocalDateTime createTime;
}
