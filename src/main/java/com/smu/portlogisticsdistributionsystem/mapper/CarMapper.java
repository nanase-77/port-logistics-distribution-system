package com.smu.portlogisticsdistributionsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smu.portlogisticsdistributionsystem.dto.CarWithPortDTO;
import com.smu.portlogisticsdistributionsystem.entity.Car;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@org.apache.ibatis.annotations.Mapper
public interface CarMapper extends BaseMapper<Car> {

    @Select("SELECT * FROM cars ")
    List<Car> selectByName(@Param("keyword") String keyword);

    @Select("SELECT c.*, p.port_name FROM cars c LEFT JOIN ports p ON c.port_id = p.id")
    IPage<CarWithPortDTO> selectWithPort(Page<CarWithPortDTO> page);
}
