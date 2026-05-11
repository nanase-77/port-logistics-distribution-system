package com.smu.portlogisticsdistributionsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("containers")
public class Container {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String content;
    private String size;
    @TableField("company_id")
    private Integer companyId;
    @TableField("update_time")
    private LocalDateTime updateTime;
    @TableField("create_time")
    private LocalDateTime createTime;
    @TableField("state")
    private int state;
}