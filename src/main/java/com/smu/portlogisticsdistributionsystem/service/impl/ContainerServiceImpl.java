package com.smu.portlogisticsdistributionsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.smu.portlogisticsdistributionsystem.dto.ContainerDTO;
import com.smu.portlogisticsdistributionsystem.dto.ContainerQueryDTO;
import com.smu.portlogisticsdistributionsystem.dto.ContainerWithCompanyDTO;
import com.smu.portlogisticsdistributionsystem.entity.Container;
import com.smu.portlogisticsdistributionsystem.mapper.ContainerMapper;
import com.smu.portlogisticsdistributionsystem.service.ContainerService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ContainerServiceImpl extends ServiceImpl<ContainerMapper, Container> implements ContainerService {
    @Override
    public Page<Container> select(int pageNum, int pageSize, ContainerQueryDTO containerQueryDTO) {
        Page<Container> p = new Page<>(pageNum, pageSize);
        QueryWrapper<Container> q = new QueryWrapper<>();
        if (StringUtils.hasText(containerQueryDTO.getContent())) {
            q.like("content", containerQueryDTO.getContent());
        }
        if (containerQueryDTO.getCapacity() != null) {
            q.eq("capacity", containerQueryDTO.getCapacity());
        }
        if (containerQueryDTO.getStatus() != null) {
            q.eq("status", containerQueryDTO.getStatus());
        }
        return baseMapper.selectPage(p, q);
    }

    @Override
    public void add(ContainerDTO containerDTO) {
        Container container = new Container();
        BeanUtils.copyProperties(containerDTO, container);
        container.setCreateTime(LocalDateTime.now());
        container.setUpdateTime(LocalDateTime.now());
        baseMapper.insert(container);
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
    public void update(ContainerDTO containerDTO) {
        Container container = new Container();
        BeanUtils.copyProperties(containerDTO, container);
        container.setUpdateTime(LocalDateTime.now());
        baseMapper.updateById(container);
    }

    public IPage<ContainerWithCompanyDTO> selectWithCompany(int pageNum, int pageSize) {
        Page<ContainerWithCompanyDTO> page = new Page<>(pageNum, pageSize);
        return baseMapper.selectWithCompany(page);
    }
}