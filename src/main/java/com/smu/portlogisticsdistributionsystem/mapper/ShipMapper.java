package com.smu.portlogisticsdistributionsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smu.portlogisticsdistributionsystem.entity.Ship;
import com.smu.portlogisticsdistributionsystem.vo.ShipVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ShipMapper extends BaseMapper<Ship> {

    @Select("SELECT s.id, s.ship_name, s.company_id, s.update_time, s.create_time, " +
            "c.company_name " +
            "FROM ships s " +
            "LEFT JOIN companies c ON s.company_id = c.id " +
            "ORDER BY s.create_time DESC")
    Page<ShipVO> selectPageWithCompany(Page<ShipVO> page);
}
