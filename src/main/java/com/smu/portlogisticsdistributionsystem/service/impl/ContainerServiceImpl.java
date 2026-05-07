package com.smu.portlogisticsdistributionsystem.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smu.portlogisticsdistributionsystem.dto.ContainerDTO;
import com.smu.portlogisticsdistributionsystem.dto.ContainerQueryDTO;
import com.smu.portlogisticsdistributionsystem.entity.Container;
import com.smu.portlogisticsdistributionsystem.mapper.ContainerMapper;
import com.smu.portlogisticsdistributionsystem.service.ContainerService;
import com.smu.portlogisticsdistributionsystem.vo.ContainerVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ContainerServiceImpl extends ServiceImpl<ContainerMapper, Container> implements ContainerService {
    @Override
    public Page<ContainerVO> select(int pageNum, int pageSize) {
        Page<ContainerVO> p = new Page<>(pageNum, pageSize);
        return baseMapper.selectPageWithCompany(p);
    }

    @Override
    public Page<ContainerVO> select(int pageNum, int pageSize, ContainerQueryDTO containerQueryDTO) {
        Page<ContainerVO> p = new Page<>(pageNum, pageSize);
        return baseMapper.selectPageWithCompanyAndCondition(p, containerQueryDTO);
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
}
