package com.smu.portlogisticsdistributionsystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smu.portlogisticsdistributionsystem.dto.OrderDTO;
import com.smu.portlogisticsdistributionsystem.dto.OrderQueryDTO;
import com.smu.portlogisticsdistributionsystem.entity.Order;

public interface OrderService {
    Page<Order> select(int pageNum, int pageSize, OrderQueryDTO orderQueryDTO);

    void add(OrderDTO orderDTO);

    void delete(String ids);

    void update(OrderDTO orderDTO);
}