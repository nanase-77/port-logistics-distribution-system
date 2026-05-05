package com.smu.portlogisticsdistributionsystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.smu.portlogisticsdistributionsystem.dto.ContainerDTO;
import com.smu.portlogisticsdistributionsystem.dto.ContainerQueryDTO;
import com.smu.portlogisticsdistributionsystem.entity.Container;

public interface ContainerService extends IService<Container> {
    Page<Container> select(int pageNum, int pageSize, ContainerQueryDTO containerQueryDTO);

    void add(ContainerDTO containerDTO);

    void delete(String ids);

    void update(ContainerDTO containerDTO);
}