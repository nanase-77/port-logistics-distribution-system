package com.smu.portlogisticsdistributionsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smu.portlogisticsdistributionsystem.entity.Car;
import com.smu.portlogisticsdistributionsystem.vo.CarVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CarMapper extends BaseMapper<Car> {

    @Select("SELECT c.id, c.` car_name` AS carName, c.image_url AS imageUrl, c.port_id AS portId, c.status, c.update_time AS updateTime, c.create_time AS createTime, " +
            "p.port_name AS portName " +
            "FROM cars c " +
            "LEFT JOIN ports p ON c.port_id = p.id " +
            "ORDER BY c.create_time DESC")
    Page<CarVO> selectPageWithPort(Page<CarVO> page);
}
