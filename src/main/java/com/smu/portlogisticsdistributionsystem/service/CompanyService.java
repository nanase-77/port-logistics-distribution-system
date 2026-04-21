package com.smu.portlogisticsdistributionsystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smu.portlogisticsdistributionsystem.dto.CompanyDTO;
import com.smu.portlogisticsdistributionsystem.dto.CompanyQueryDTO;
import com.smu.portlogisticsdistributionsystem.entity.Company;

public interface CompanyService {
    Page<Company> select(int pageNum, int pageSize, CompanyQueryDTO companyQueryDTO);

    void add(CompanyDTO companyDTO);

    void delete(String ids);

    void update(CompanyDTO companyDTO);
}