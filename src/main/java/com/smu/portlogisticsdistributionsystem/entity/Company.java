package com.smu.portlogisticsdistributionsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("companies")
public class Company {
    @TableId(type = IdType.AUTO)
    private Integer id;
    @TableField("company_name")
    private String companyName;
    private String country;
    @TableField("update_time")
    private LocalDateTime updateTime;
    @TableField("create_time")
    private LocalDateTime createTime;
}