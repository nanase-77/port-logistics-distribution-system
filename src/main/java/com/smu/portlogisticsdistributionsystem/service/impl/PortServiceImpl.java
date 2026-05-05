package com.smu.portlogisticsdistributionsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smu.portlogisticsdistributionsystem.dto.PortDTO;
import com.smu.portlogisticsdistributionsystem.dto.PortQueryDTO;
import com.smu.portlogisticsdistributionsystem.entity.Port;
import com.smu.portlogisticsdistributionsystem.mapper.PortMapper;
import com.smu.portlogisticsdistributionsystem.service.PortService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PortServiceImpl extends ServiceImpl<PortMapper, Port> implements PortService {
    @Override
    public Page<Port> select(int pageNum, int pageSize, PortQueryDTO portQueryDTO) {
        Page<Port> p = new Page<>(pageNum, pageSize);
        QueryWrapper<Port> q = new QueryWrapper<>();
        if (portQueryDTO != null && StringUtils.hasText(portQueryDTO.getPortName())) {
            q.like("port_name", portQueryDTO.getPortName());
        }
        return baseMapper.selectPage(p, q);
    }

    @Override
    public void add(PortDTO portDTO) {
        Port port = new Port();
        BeanUtils.copyProperties(portDTO, port);
        port.setCreateTime(LocalDateTime.now());
        port.setUpdateTime(LocalDateTime.now());
        baseMapper.insert(port);
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
    public void update(PortDTO portDTO) {
        Port port = new Port();
        BeanUtils.copyProperties(portDTO, port);
        port.setUpdateTime(LocalDateTime.now());
        baseMapper.updateById(port);
    }
}
