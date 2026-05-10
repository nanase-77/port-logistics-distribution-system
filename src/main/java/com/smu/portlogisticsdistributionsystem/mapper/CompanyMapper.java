package com.smu.portlogisticsdistributionsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smu.portlogisticsdistributionsystem.entity.Company;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CompanyMapper extends BaseMapper<Company> {

    @Select("SELECT * FROM companies WHERE company_name LIKE CONCAT('%', #{keyword}, '%')")
    List<Company> selectByName(@Param("keyword") String keyword);
}
