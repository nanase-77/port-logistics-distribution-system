package com.smu.portlogisticsdistributionsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smu.portlogisticsdistributionsystem.dto.CompanyDTO;
import com.smu.portlogisticsdistributionsystem.dto.CompanyQueryDTO;
import com.smu.portlogisticsdistributionsystem.entity.Company;
import com.smu.portlogisticsdistributionsystem.mapper.CompanyMapper;
import com.smu.portlogisticsdistributionsystem.service.CompanyService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CompanyServiceImpl extends ServiceImpl<CompanyMapper, Company> implements CompanyService {
    @Override
    public Page<Company> select(int pageNum, int pageSize, CompanyQueryDTO companyQueryDTO) {
        Page<Company> p = new Page<>(pageNum, pageSize);
        QueryWrapper<Company> q = new QueryWrapper<>();
        if (StringUtils.hasText(companyQueryDTO.getCompanyName())) {
            q.like("company_name", companyQueryDTO.getCompanyName());
        }
        if (StringUtils.hasText(companyQueryDTO.getCountry())) {
            q.eq("country", companyQueryDTO.getCountry());
        }
        return baseMapper.selectPage(p, q);
    }

    @Override
    public void add(CompanyDTO companyDTO) {
        Company company = new Company();
        BeanUtils.copyProperties(companyDTO, company);
        company.setCreateTime(LocalDateTime.now());
        company.setUpdateTime(LocalDateTime.now());
        baseMapper.insert(company);
    }

    @Override
    public void delete(String ids) {
        String[] idArray = ids.split(",");
        List<Integer> idList = new ArrayList<>();
        for (String id : idArray) {
            idList.add(Integer.valueOf(id));
        }
        baseMapper.deleteBatchIds(idList);
    }

    @Override
    public void update(CompanyDTO companyDTO) {
        Company company = new Company();
        BeanUtils.copyProperties(companyDTO, company);
        company.setUpdateTime(LocalDateTime.now());
        baseMapper.updateById(company);
    }
}