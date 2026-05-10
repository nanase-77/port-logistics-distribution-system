package com.smu.portlogisticsdistributionsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smu.portlogisticsdistributionsystem.dto.OrderDTO;
import com.smu.portlogisticsdistributionsystem.dto.OrderQueryDTO;
import com.smu.portlogisticsdistributionsystem.entity.Order;
import com.smu.portlogisticsdistributionsystem.mapper.OrderMapper;
import com.smu.portlogisticsdistributionsystem.service.OrderService;
import com.smu.portlogisticsdistributionsystem.util.UserHolder;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    @Override
    public Page<Order> select(int pageNum, int pageSize, OrderQueryDTO orderQueryDTO) {
        Page<Order> p = new Page<>(pageNum, pageSize);
        QueryWrapper<Order> q = new QueryWrapper<>();
        if (orderQueryDTO != null) {
            if (StringUtils.hasText(orderQueryDTO.getOrderNumber())) {
                q.like("order_number", orderQueryDTO.getOrderNumber());
            }
            if (orderQueryDTO.getUserId() != null) {
                q.eq("user_id", UserHolder.getUser().getId());
            }
            if (StringUtils.hasText(orderQueryDTO.getStatus())) {
                q.eq("status", orderQueryDTO.getStatus());
            }
        }
        return baseMapper.selectPage(p, q);
    }

    @Override
    public void add(OrderDTO orderDTO) {
        Order order = new Order();
        BeanUtils.copyProperties(orderDTO, order);
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        baseMapper.insert(order);
    }

    @Override
    public void delete(String ids) {
        String[] idArray = ids.split(",");
        List<Integer> idList = new ArrayList<>();
        for (String id : idArray) {
            idList.add(Integer.valueOf(id));
        }
        baseMapper.deleteBatchIds(idList);
    }

    @Override
    public void update(OrderDTO orderDTO) {
        Order order = new Order();
        BeanUtils.copyProperties(orderDTO, order);
        order.setUpdateTime(LocalDateTime.now());
        baseMapper.updateById(order);
    }

    @Override
    public Page<Order> selectByUserId(int pageNum, int pageSize, Integer userId) {
        Page<Order> p = new Page<>(pageNum, pageSize);
        QueryWrapper<Order> q = new QueryWrapper<>();
        q.eq("user_id", userId);
        return baseMapper.selectPage(p, q);
    }

    @Override
    public void addByUser(OrderDTO orderDTO, Integer userId) {
        Order order = new Order();
        BeanUtils.copyProperties(orderDTO, order);
        order.setUserId(userId);
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        baseMapper.insert(order);
    }

    @Override
    public Integer getOrderCount() {
        return Integer.parseInt(String.valueOf(count()));
    }
}