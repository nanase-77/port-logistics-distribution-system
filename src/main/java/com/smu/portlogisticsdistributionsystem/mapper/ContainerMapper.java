package com.smu.portlogisticsdistributionsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smu.portlogisticsdistributionsystem.dto.ContainerWithCompanyDTO;
import com.smu.portlogisticsdistributionsystem.entity.Container;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ContainerMapper extends BaseMapper<Container> {

    @Select("SELECT * FROM containers WHERE container_no LIKE CONCAT('%', #{keyword}, '%') OR content LIKE CONCAT('%', #{keyword}, '%')")
    List<Container> selectByKeyword(@Param("keyword") String keyword);

    @Select("SELECT c.*, cp.company_name FROM containers c LEFT JOIN companies cp ON c.company_id = cp.id")
    IPage<ContainerWithCompanyDTO> selectWithCompany(Page<ContainerWithCompanyDTO> page);
}
