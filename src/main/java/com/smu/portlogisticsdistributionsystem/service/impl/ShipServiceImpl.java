package com.smu.portlogisticsdistributionsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smu.portlogisticsdistributionsystem.dto.ShipDTO;
import com.smu.portlogisticsdistributionsystem.entity.Ship;
import com.smu.portlogisticsdistributionsystem.mapper.ShipMapper;
import com.smu.portlogisticsdistributionsystem.service.ShipService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ShipServiceImpl extends ServiceImpl<ShipMapper, Ship> implements ShipService {
    @Override
    public Page<Ship> select(int pageNum, int pageSize) {
        Page<Ship> p = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Ship> q = new LambdaQueryWrapper<>();
        return baseMapper.selectPage(p, q);
    }

    @Override
    public void add(ShipDTO shipDTO) {
        Ship ship = new Ship();
        BeanUtils.copyProperties(shipDTO, ship);
        ship.setCreateTime(LocalDateTime.now());
        ship.setUpdateTime(LocalDateTime.now());
        baseMapper.insert(ship);
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
    public void update(ShipDTO shipDTO) {
        Ship ship = new Ship();
        BeanUtils.copyProperties(shipDTO, ship);
        ship.setUpdateTime(LocalDateTime.now());
        baseMapper.updateById(ship);
    }
}