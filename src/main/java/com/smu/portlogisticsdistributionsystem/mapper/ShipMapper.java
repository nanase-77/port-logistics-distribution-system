package com.smu.portlogisticsdistributionsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smu.portlogisticsdistributionsystem.entity.Ship;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ShipMapper extends BaseMapper<Ship> {

    @Select("SELECT * FROM ships WHERE ship_name LIKE CONCAT('%', #{keyword}, '%')")
    List<Ship> selectByName(@Param("keyword") String keyword);
}
