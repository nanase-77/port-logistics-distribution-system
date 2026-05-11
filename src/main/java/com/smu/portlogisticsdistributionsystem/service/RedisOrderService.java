package com.smu.portlogisticsdistributionsystem.service;

import com.smu.portlogisticsdistributionsystem.entity.Order;

import java.util.List;

public interface RedisOrderService {
    void addOrder(Order order);
    void updateOrder(Order order);
    void deleteOrder(Integer orderId);
    void clearAllOrders();
    List<Order> getAllOrders();
    Order getOrderById(Integer orderId);
}
