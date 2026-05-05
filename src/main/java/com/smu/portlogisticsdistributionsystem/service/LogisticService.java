package com.smu.portlogisticsdistributionsystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.smu.portlogisticsdistributionsystem.dto.LogisticDTO;
import com.smu.portlogisticsdistributionsystem.dto.LogisticQueryDTO;
import com.smu.portlogisticsdistributionsystem.entity.Logistic;

import java.util.List;

public interface LogisticService extends IService<Logistic> {
    Page<Logistic> select(int pageNum, int pageSize, LogisticQueryDTO logisticQueryDTO);

    void add(LogisticDTO logisticDTO);

    void delete(String ids);

    void update(LogisticDTO logisticDTO);

    Page<Logistic> selectByOrderIds(int pageNum, int pageSize, List<Integer> orderIds);
}