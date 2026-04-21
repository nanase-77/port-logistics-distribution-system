package com.smu.portlogisticsdistributionsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smu.portlogisticsdistributionsystem.dto.LogisticDTO;
import com.smu.portlogisticsdistributionsystem.dto.LogisticQueryDTO;
import com.smu.portlogisticsdistributionsystem.entity.Logistic;
import com.smu.portlogisticsdistributionsystem.mapper.LogisticMapper;
import com.smu.portlogisticsdistributionsystem.service.LogisticService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class LogisticServiceImpl extends ServiceImpl<LogisticMapper, Logistic> implements LogisticService {
    @Override
    public Page<Logistic> select(int pageNum, int pageSize, LogisticQueryDTO logisticQueryDTO) {
        Page<Logistic> p = new Page<>(pageNum, pageSize);
        QueryWrapper<Logistic> q = new QueryWrapper<>();
        if (logisticQueryDTO.getOrderId() != null) {
            q.eq("order_id", logisticQueryDTO.getOrderId());
        }
        if (logisticQueryDTO.getStartPortId() != null) {
            q.eq("start_port_id", logisticQueryDTO.getStartPortId());
        }
        if (logisticQueryDTO.getEndPortId() != null) {
            q.eq("end_port_id", logisticQueryDTO.getEndPortId());
        }
        if (logisticQueryDTO.getCurrentPortId() != null) {
            q.eq("current_port_id", logisticQueryDTO.getCurrentPortId());
        }
        if (logisticQueryDTO.getShipId() != null) {
            q.eq("ship_id", logisticQueryDTO.getShipId());
        }
        if (logisticQueryDTO.getCarId() != null) {
            q.eq("car_id", logisticQueryDTO.getCarId());
        }
        return baseMapper.selectPage(p, q);
    }

    @Override
    public void add(LogisticDTO logisticDTO) {
        Logistic logistic = new Logistic();
        BeanUtils.copyProperties(logisticDTO, logistic);
        logistic.setCreateTime(LocalDateTime.now());
        logistic.setUpdateTime(LocalDateTime.now());
        baseMapper.insert(logistic);
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
    public void update(LogisticDTO logisticDTO) {
        Logistic logistic = new Logistic();
        BeanUtils.copyProperties(logisticDTO, logistic);
        logistic.setUpdateTime(LocalDateTime.now());
        baseMapper.updateById(logistic);
    }
}