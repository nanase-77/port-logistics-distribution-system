package com.smu.portlogisticsdistributionsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("orders")
public class Order {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String orderNumber;
    private Integer userId;
    private String status;
    private String containerIds;
    private LocalDateTime updateTime;
    private LocalDateTime createTime;
}