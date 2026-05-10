package com.smu.portlogisticsdistributionsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smu.portlogisticsdistributionsystem.entity.Port;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface PortMapper extends BaseMapper<Port> {

    @Select("SELECT * FROM ports WHERE port_name LIKE CONCAT('%', #{keyword}, '%')")
    List<Port> selectByName(@Param("keyword") String keyword);

    @Select("SELECT * FROM ports WHERE max_tonnage >= #{tonnage}")
    List<Port> selectByTonnage(@Param("tonnage") Integer tonnage);
}
