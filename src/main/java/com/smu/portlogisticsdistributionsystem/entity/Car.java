package com.smu.portlogisticsdistributionsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("cars")
public class Car {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String carName;
    private String imageUrl;
    private Integer portId;
    private String status;
    private LocalDateTime updateTime;
    private LocalDateTime createTime;
}