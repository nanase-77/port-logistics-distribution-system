package com.smu.portlogisticsdistributionsystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.smu.portlogisticsdistributionsystem.dto.CarDTO;
import com.smu.portlogisticsdistributionsystem.dto.CarQueryDTO;
import com.smu.portlogisticsdistributionsystem.entity.Car;

public interface CarService extends IService<Car> {
    Page<Car> select(int pageNum, int pageSize, CarQueryDTO carQueryDTO);

    void add(CarDTO carDTO);

    void delete(String ids);

    void update(CarDTO carDTO);
}