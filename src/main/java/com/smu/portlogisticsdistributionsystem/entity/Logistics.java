package com.smu.portlogisticsdistributionsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("logistics")
public class Logistics {
    @TableId(type = IdType.AUTO)
    private Integer id;
    @TableField("order_id")
    private Integer orderId;
    @TableField("start_port_id")
    private Integer startPortId;
    @TableField("end_port_id")
    private Integer endPortId;
    @TableField("current_port_id")
    private Integer currentPortId;
    @TableField("ship_id")
    private Integer shipId;
    @TableField("car_id")
    private Integer carId;
    @TableField("update_time")
    private LocalDateTime updateTime;
    @TableField("create_time")
    private LocalDateTime createTime;
}