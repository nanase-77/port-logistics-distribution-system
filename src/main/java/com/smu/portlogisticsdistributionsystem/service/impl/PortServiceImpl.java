package com.smu.portlogisticsdistributionsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smu.portlogisticsdistributionsystem.common.Result;
import com.smu.portlogisticsdistributionsystem.dto.PortDTO;
import com.smu.portlogisticsdistributionsystem.dto.PortQueryDTO;
import com.smu.portlogisticsdistributionsystem.entity.Port;
import com.smu.portlogisticsdistributionsystem.mapper.PortMapper;
import com.smu.portlogisticsdistributionsystem.service.PortGraphService;
import com.smu.portlogisticsdistributionsystem.service.PortService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PortServiceImpl extends ServiceImpl<PortMapper, Port> implements PortService {

    private final PortGraphService portGraphService;
    @Override
    public Page<Port> select(int pageNum, int pageSize, PortQueryDTO portQueryDTO) {
        Page<Port> p=new Page<>(pageNum,pageSize);
        QueryWrapper q=new QueryWrapper();
        if(StringUtils.hasText(portQueryDTO.getPortName())){
            q.like("port_name",portQueryDTO.getPortName());
        }
        return baseMapper.selectPage(p,q);
    }

    @Override
    @Transactional
    public void add(PortDTO portDTO) {
        Port p=new Port();
        BeanUtils.copyProperties(portDTO,p);
        p.setCreateTime(LocalDateTime.now());
        p.setUpdateTime(LocalDateTime.now());
        baseMapper.insert(p);
        
        portGraphService.addPortToGraph(p);
        log.info("Port {} added to MySQL and Neo4j", p.getPortName());
    }

    @Override
    @Transactional
    public void delete(String ids) {
        String[] id=ids.split(",");
        List<Integer> idss=new ArrayList<>();
        for(int i=0;i<id.length;i++){
            idss.add(Integer.valueOf(id[i]));
        }
        
        for (Integer portId : idss) {
            portGraphService.removePortFromGraph(portId.longValue());
        }
        
        baseMapper.deleteBatchIds(idss);
        log.info("Ports {} deleted from MySQL and Neo4j", ids);
    }

    @Override
    @Transactional
    public void update(PortDTO portDTO) {
        Port port=new Port();
        BeanUtils.copyProperties(portDTO,port);
        port.setUpdateTime(LocalDateTime.now());
        baseMapper.updateById(port);
        
        portGraphService.updatePortInGraph(port);
        log.info("Port {} updated in MySQL and Neo4j", port.getPortName());
    }

}
