package com.smu.portlogisticsdistributionsystem.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.smu.portlogisticsdistributionsystem.dto.OrderDTO;
import com.smu.portlogisticsdistributionsystem.dto.OrderQueryDTO;
import com.smu.portlogisticsdistributionsystem.entity.Order;

import java.util.List;

public interface OrderService extends IService<Order> {
    Page<Order> select(int pageNum, int pageSize, OrderQueryDTO orderQueryDTO);

    void add(OrderDTO orderDTO);

    void delete(String ids);

    void update(OrderDTO orderDTO);

    Page<Order> selectByUserId(int pageNum, int pageSize, Integer userId);

    void addByUser(OrderDTO orderDTO, Integer userId);

}