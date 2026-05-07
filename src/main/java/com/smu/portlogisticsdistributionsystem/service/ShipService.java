package com.smu.portlogisticsdistributionsystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.smu.portlogisticsdistributionsystem.dto.ShipDTO;
import com.smu.portlogisticsdistributionsystem.entity.Ship;
import com.smu.portlogisticsdistributionsystem.vo.ShipVO;

public interface ShipService extends IService<Ship> {
    Page<ShipVO> select(int pageNum, int pageSize);

    void add(ShipDTO shipDTO);

    void delete(String ids);

    void update(ShipDTO shipDTO);
}
