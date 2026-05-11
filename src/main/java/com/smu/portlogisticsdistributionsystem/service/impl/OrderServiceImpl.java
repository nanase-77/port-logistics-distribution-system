package com.smu.portlogisticsdistributionsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smu.portlogisticsdistributionsystem.dto.OrderDTO;
import com.smu.portlogisticsdistributionsystem.dto.OrderQueryDTO;
import com.smu.portlogisticsdistributionsystem.dto.OrderStatsDTO;
import com.smu.portlogisticsdistributionsystem.dto.UserLoginDTO;
import com.smu.portlogisticsdistributionsystem.entity.Order;
import com.smu.portlogisticsdistributionsystem.mapper.OrderMapper;
import com.smu.portlogisticsdistributionsystem.service.OrderService;
import com.smu.portlogisticsdistributionsystem.service.RedisOrderService;
import com.smu.portlogisticsdistributionsystem.util.UserHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final RedisOrderService redisOrderService;

    @Override
    public Page<Order> select(int pageNum, int pageSize, OrderQueryDTO orderQueryDTO) {
        Page<Order> p = new Page<>(pageNum, pageSize);
        QueryWrapper<Order> q = new QueryWrapper<>();
        if (StringUtils.hasText(orderQueryDTO.getOrderNumber())) {
            q.like("order_number", orderQueryDTO.getOrderNumber());
        }
        if (orderQueryDTO.getUserId() != null) {
            q.eq("user_id", orderQueryDTO.getUserId());
        }
        if (StringUtils.hasText(orderQueryDTO.getStatus())) {
            q.eq("status", orderQueryDTO.getStatus());
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
        redisOrderService.clearAllOrders();
        log.info("Order {} added to MySQL, Redis cache cleared", order.getId());
    }

    @Override
    public void delete(String ids) {
        String[] idArray = ids.split(",");
        List<Integer> idList = new ArrayList<>();
        for (String id : idArray) {
            idList.add(Integer.valueOf(id));
        }
        baseMapper.deleteBatchIds(idList);
        redisOrderService.clearAllOrders();
        log.info("Orders {} deleted from MySQL, Redis cache cleared", ids);
    }

    @Override
    public void update(OrderDTO orderDTO) {
        Order order = new Order();
        BeanUtils.copyProperties(orderDTO, order);
        order.setUpdateTime(LocalDateTime.now());
        baseMapper.updateById(order);
        redisOrderService.clearAllOrders();
        log.info("Order {} updated in MySQL, Redis cache cleared", order.getId());
    }

    public List<Order> selectFromCacheOrDb() {
        try {
            UserLoginDTO currentUser = UserHolder.getUser();
            Integer userId = currentUser != null ? currentUser.getId() : null;
            
            List<Order> cachedOrders = redisOrderService.getAllOrders();
            log.info("Redis cached orders count: {}", cachedOrders.size());
            
            List<Order> userOrders;
            if (!cachedOrders.isEmpty()) {
                if (userId != null) {
                    userOrders = cachedOrders.stream()
                            .filter(order -> userId.equals(order.getUserId()))
                            .toList();
                    log.info("Filtered user {} orders from Redis: {}", userId, userOrders.size());
                } else {
                    userOrders = cachedOrders;
                }
            } else {
                log.info("No orders in Redis cache, fetching from MySQL");
                QueryWrapper<Order> q = new QueryWrapper<>();
                if (userId != null) {
                    q.eq("user_id", userId);
                }
                userOrders = baseMapper.selectList(q);
                log.info("MySQL orders count for user {}: {}", userId, userOrders.size());
                
                for (Order order : userOrders) {
                    redisOrderService.addOrder(order);
                }
            }
            return userOrders;
        } catch (Exception e) {
            log.error("Error in selectFromCacheOrDb: ", e);
            throw e;
        }
    }

    public OrderStatsDTO getOrderStats() {
        UserLoginDTO currentUser = UserHolder.getUser();
        Integer userId = currentUser != null ? currentUser.getId() : null;
        
        OrderStatsDTO stats = new OrderStatsDTO();
        
        QueryWrapper<Order> pendingQ = new QueryWrapper<>();
        if (userId != null) {
            pendingQ.eq("user_id", userId);
        }
        pendingQ.eq("status", "待处理");
        stats.setPendingCount((Integer.parseInt(String.valueOf(baseMapper.selectCount(pendingQ)))));
        
        QueryWrapper<Order> progressQ = new QueryWrapper<>();
        if (userId != null) {
            progressQ.eq("user_id", userId);
        }
        progressQ.eq("status", "进行中");
        stats.setProgressCount((Integer.parseInt(String.valueOf(baseMapper.selectCount(progressQ)))));
        
        QueryWrapper<Order> completedQ = new QueryWrapper<>();
        if (userId != null) {
            completedQ.eq("user_id", userId);
        }
        completedQ.eq("status", "已完成");
        stats.setCompletedCount((Integer.parseInt(String.valueOf(baseMapper.selectCount(completedQ)))));
        
        return stats;
    }
}
