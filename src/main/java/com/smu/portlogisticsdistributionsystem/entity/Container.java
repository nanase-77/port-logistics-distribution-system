package com.smu.portlogisticsdistributionsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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
    private Integer companyId;
    private LocalDateTime updateTime;
    private LocalDateTime createTime;
}