package com.smu.portlogisticsdistributionsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("logistics")
public class Logistic {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer orderId;
    private Integer startPortId;
    private Integer endPortId;
    private Integer currentPortId;
    private Integer shipId;
    private Integer carId;
    private LocalDateTime updateTime;
    private LocalDateTime createTime;
}