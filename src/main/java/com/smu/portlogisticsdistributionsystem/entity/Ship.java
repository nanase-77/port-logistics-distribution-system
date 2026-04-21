package com.smu.portlogisticsdistributionsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("ships")
public class Ship {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String shipName;
    private String companyId;
    private LocalDateTime updateTime;
    private LocalDateTime createTime;
}