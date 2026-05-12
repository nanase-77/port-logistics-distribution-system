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

    @Select("SELECT c.id, c.content, c.capacity, c.status, c.create_time, c.update_time FROM containers c")
    IPage<ContainerWithCompanyDTO> selectWithCompany(Page<ContainerWithCompanyDTO> page);
}