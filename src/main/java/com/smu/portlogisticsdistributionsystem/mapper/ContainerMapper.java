package com.smu.portlogisticsdistributionsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smu.portlogisticsdistributionsystem.dto.ContainerQueryDTO;
import com.smu.portlogisticsdistributionsystem.entity.Container;
import com.smu.portlogisticsdistributionsystem.vo.ContainerVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ContainerMapper extends BaseMapper<Container> {

    @Select("SELECT c.id, c.content, c.size, c.company_id, c.update_time, c.create_time, " +
            "co.company_name " +
            "FROM containers c " +
            "LEFT JOIN companies co ON c.company_id = co.id " +
            "ORDER BY c.create_time DESC")
    Page<ContainerVO> selectPageWithCompany(Page<ContainerVO> page);

    @Select("<script>" +
            "SELECT c.id, c.content, c.size, c.company_id, c.update_time, c.create_time, " +
            "co.company_name " +
            "FROM containers c " +
            "LEFT JOIN companies co ON c.company_id = co.id " +
            "WHERE 1=1 " +
            "<if test='query.content != null and query.content != \"\"'>" +
            "AND c.content LIKE CONCAT('%', #{query.content}, '%') " +
            "</if>" +
            "<if test='query.size != null and query.size != \"\"'>" +
            "AND c.size = #{query.size} " +
            "</if>" +
            "<if test='query.companyId != null'>" +
            "AND c.company_id = #{query.companyId} " +
            "</if>" +
            "ORDER BY c.create_time DESC" +
            "</script>")
    Page<ContainerVO> selectPageWithCompanyAndCondition(Page<ContainerVO> page, @Param("query") ContainerQueryDTO query);
}
