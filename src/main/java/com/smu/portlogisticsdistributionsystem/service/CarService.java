package com.smu.portlogisticsdistributionsystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.smu.portlogisticsdistributionsystem.dto.CarDTO;
import com.smu.portlogisticsdistributionsystem.dto.CarQueryDTO;
import com.smu.portlogisticsdistributionsystem.entity.Car;
import com.smu.portlogisticsdistributionsystem.vo.CarVO;

public interface CarService extends IService<Car> {
    Page<CarVO> select(int pageNum, int pageSize);

    void add(CarDTO carDTO);

    void delete(String ids);

    void update(CarDTO carDTO);
}
