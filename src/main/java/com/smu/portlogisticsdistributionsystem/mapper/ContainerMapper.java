package com.smu.portlogisticsdistributionsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smu.portlogisticsdistributionsystem.entity.Container;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ContainerMapper extends BaseMapper<Container> {

    @Select("SELECT * FROM containers WHERE container_no LIKE CONCAT('%', #{keyword}, '%') OR content LIKE CONCAT('%', #{keyword}, '%')")
    List<Container> selectByKeyword(@Param("keyword") String keyword);
}
