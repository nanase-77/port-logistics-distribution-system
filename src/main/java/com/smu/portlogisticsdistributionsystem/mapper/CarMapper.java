package com.smu.portlogisticsdistributionsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smu.portlogisticsdistributionsystem.entity.Car;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@org.apache.ibatis.annotations.Mapper
public interface CarMapper extends BaseMapper<Car> {

    @Select("SELECT * FROM cars WHERE car_name LIKE CONCAT('%', #{keyword}, '%')")
    List<Car> selectByName(@Param("keyword") String keyword);
}
