package com.smu.portlogisticsdistributionsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smu.portlogisticsdistributionsystem.dto.ShipWithCompanyDTO;
import com.smu.portlogisticsdistributionsystem.entity.Ship;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ShipMapper extends BaseMapper<Ship> {

    @Select("SELECT * FROM ships WHERE ship_name LIKE CONCAT('%', #{keyword}, '%')")
    List<Ship> selectByName(@Param("keyword") String keyword);

    @Select("<script>" +
            "SELECT s.id, s.ship_name, s.company_id, s.update_time, s.create_time, c.company_name " +
            "FROM ships s LEFT JOIN companies c ON s.company_id = c.id " +
            "<where>" +
            "<if test='shipName != null and shipName != \"\"'>" +
            "AND s.ship_name LIKE CONCAT('%', #{shipName}, '%')" +
            "</if>" +
            "<if test='companyId != null and companyId != \"\"'>" +
            "AND s.company_id = #{companyId}" +
            "</if>" +
            "</where>" +
            "ORDER BY s.create_time DESC" +
            "</script>")
    IPage<ShipWithCompanyDTO> selectWithCompany(Page<ShipWithCompanyDTO> page, @Param("shipName") String shipName, @Param("companyId") String companyId);
}
