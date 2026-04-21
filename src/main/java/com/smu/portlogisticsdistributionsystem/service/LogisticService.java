package com.smu.portlogisticsdistributionsystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smu.portlogisticsdistributionsystem.dto.LogisticDTO;
import com.smu.portlogisticsdistributionsystem.dto.LogisticQueryDTO;
import com.smu.portlogisticsdistributionsystem.entity.Logistic;

public interface LogisticService {
    Page<Logistic> select(int pageNum, int pageSize, LogisticQueryDTO logisticQueryDTO);

    void add(LogisticDTO logisticDTO);

    void delete(String ids);

    void update(LogisticDTO logisticDTO);
}