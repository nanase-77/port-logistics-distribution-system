package com.smu.portlogisticsdistributionsystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smu.portlogisticsdistributionsystem.dto.ShipDTO;
import com.smu.portlogisticsdistributionsystem.dto.ShipQueryDTO;
import com.smu.portlogisticsdistributionsystem.dto.ShipWithCompanyDTO;
import com.smu.portlogisticsdistributionsystem.entity.Ship;

public interface ShipService {
    IPage<ShipWithCompanyDTO> select(int pageNum, int pageSize, ShipQueryDTO shipQueryDTO);

    void add(ShipDTO shipDTO);

    void delete(String ids);

    void update(ShipDTO shipDTO);
}