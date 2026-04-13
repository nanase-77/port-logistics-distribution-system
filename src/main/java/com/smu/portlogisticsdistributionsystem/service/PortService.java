package com.smu.portlogisticsdistributionsystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smu.portlogisticsdistributionsystem.common.Result;
import com.smu.portlogisticsdistributionsystem.dto.PortDTO;
import com.smu.portlogisticsdistributionsystem.dto.PortQueryDTO;
import com.smu.portlogisticsdistributionsystem.entity.Port;

import java.util.List;

public interface PortService {
    Page<Port> select(int pageNum, int pageSize, PortQueryDTO portQueryDTO);

    void add(PortDTO portDTO);

    void delete(String ids);

    void update(PortDTO portDTO);
}
