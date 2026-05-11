package com.smu.portlogisticsdistributionsystem.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.smu.portlogisticsdistributionsystem.entity.Order;
import com.smu.portlogisticsdistributionsystem.service.RedisOrderService;
import com.smu.portlogisticsdistributionsystem.util.RedisConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisOrderServiceImpl implements RedisOrderService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Override
    public void addOrder(Order order) {
        try {
            String orderJson = objectMapper.writeValueAsString(order);
            redisTemplate.opsForHash().put(RedisConstants.PLDS_ORDERS, String.valueOf(order.getId()), orderJson);
            log.info("Order {} added to Redis", order.getId());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize order", e);
        }
    }

    @Override
    public void updateOrder(Order order) {
        addOrder(order);
    }

    @Override
    public void deleteOrder(Integer orderId) {
        redisTemplate.opsForHash().delete(RedisConstants.PLDS_ORDERS, String.valueOf(orderId));
        log.info("Order {} deleted from Redis", orderId);
    }

    @Override
    public void clearAllOrders() {
        redisTemplate.delete(RedisConstants.PLDS_ORDERS);
        log.info("All orders cleared from Redis");
    }

    @Override
    public List<Order> getAllOrders() {
        Map<Object, Object> orderMap = redisTemplate.opsForHash().entries(RedisConstants.PLDS_ORDERS);
        List<Order> orders = new ArrayList<>();
        for (Object value : orderMap.values()) {
            try {
                orders.add(objectMapper.readValue(value.toString(), Order.class));
            } catch (JsonProcessingException e) {
                log.error("Failed to deserialize order", e);
            }
        }
        return orders;
    }

    @Override
    public Order getOrderById(Integer orderId) {
        Object orderJson = redisTemplate.opsForHash().get(RedisConstants.PLDS_ORDERS, String.valueOf(orderId));
        if (orderJson != null) {
            try {
                return objectMapper.readValue(orderJson.toString(), Order.class);
            } catch (JsonProcessingException e) {
                log.error("Failed to deserialize order", e);
            }
        }
        return null;
    }
}
