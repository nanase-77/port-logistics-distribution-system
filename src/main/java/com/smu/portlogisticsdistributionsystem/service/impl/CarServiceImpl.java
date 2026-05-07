package com.smu.portlogisticsdistributionsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smu.portlogisticsdistributionsystem.dto.CarDTO;
import com.smu.portlogisticsdistributionsystem.dto.CarQueryDTO;
import com.smu.portlogisticsdistributionsystem.entity.Car;
import com.smu.portlogisticsdistributionsystem.mapper.CarMapper;
import com.smu.portlogisticsdistributionsystem.service.CarService;
import com.smu.portlogisticsdistributionsystem.vo.CarVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CarServiceImpl extends ServiceImpl<CarMapper, Car> implements CarService {
    @Override
    public Page<CarVO> select(int pageNum, int pageSize) {
        Page<CarVO> p = new Page<>(pageNum, pageSize);
        return baseMapper.selectPageWithPort(p);
    }

    @Override
    public void add(CarDTO carDTO) {
        Car car = new Car();
        BeanUtils.copyProperties(carDTO, car);
        car.setCreateTime(LocalDateTime.now());
        car.setUpdateTime(LocalDateTime.now());
        baseMapper.insert(car);
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
    public void update(CarDTO carDTO) {
        Car car = new Car();
        BeanUtils.copyProperties(carDTO, car);
        car.setUpdateTime(LocalDateTime.now());
        baseMapper.updateById(car);
    }
}
