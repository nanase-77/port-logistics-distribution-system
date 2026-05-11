package com.smu.portlogisticsdistributionsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smu.portlogisticsdistributionsystem.dto.LogisticDTO;
import com.smu.portlogisticsdistributionsystem.dto.LogisticQueryDTO;
import com.smu.portlogisticsdistributionsystem.dto.UserLoginDTO;
import com.smu.portlogisticsdistributionsystem.entity.Logistic;
import com.smu.portlogisticsdistributionsystem.entity.Order;
import com.smu.portlogisticsdistributionsystem.mapper.LogisticMapper;
import com.smu.portlogisticsdistributionsystem.mapper.OrderMapper;
import com.smu.portlogisticsdistributionsystem.service.LogisticService;
import com.smu.portlogisticsdistributionsystem.service.PortGraphService;
import com.smu.portlogisticsdistributionsystem.service.RedisLogisticService;
import com.smu.portlogisticsdistributionsystem.util.UserHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogisticServiceImpl extends ServiceImpl<LogisticMapper, Logistic> implements LogisticService {

    private final PortGraphService portGraphService;
    private final RedisLogisticService redisLogisticService;
    private final OrderMapper orderMapper;

    @Override
    public Page<Logistic> select(int pageNum, int pageSize, LogisticQueryDTO logisticQueryDTO) {
        Page<Logistic> p = new Page<>(pageNum, pageSize);
        QueryWrapper<Logistic> q = new QueryWrapper<>();
        if (logisticQueryDTO != null) {
            Integer orderId = logisticQueryDTO.getOrderId();
            Integer startPortId = logisticQueryDTO.getStartPortId();
            Integer endPortId = logisticQueryDTO.getEndPortId();
            Integer currentPortId = logisticQueryDTO.getCurrentPortId();
            Integer shipId = logisticQueryDTO.getShipId();
            Integer carId = logisticQueryDTO.getCarId();

            if (orderId != null) {
                q.eq("order_id", orderId);
            }
            if (startPortId != null) {
                q.eq("start_port_id", startPortId);
            }
            if (endPortId != null) {
                q.eq("end_port_id", endPortId);
            }
            if (currentPortId != null) {
                q.eq("current_port_id", currentPortId);
            }
            if (shipId != null) {
                q.eq("ship_id", shipId);
            }
            if (carId != null) {
                q.eq("car_id", carId);
            }
        }
        return baseMapper.selectPage(p, q);
    }

    @Override
    @Transactional
    public void add(LogisticDTO logisticDTO) {
        Logistic logistic = new Logistic();
        BeanUtils.copyProperties(logisticDTO, logistic);
        logistic.setCreateTime(LocalDateTime.now());
        logistic.setUpdateTime(LocalDateTime.now());
        baseMapper.insert(logistic);

        portGraphService.addLogisticToGraph(logistic);
        redisLogisticService.clearAllLogistics();
        log.info("Logistic {} added to MySQL and Neo4j, Redis cache cleared", logistic.getId());
    }

    @Override
    @Transactional
    public void delete(String ids) {
        String[] idArray = ids.split(",");
        List<Integer> idList = new ArrayList<>();
        for (String id : idArray) {
            Integer logisticId = Integer.valueOf(id);
            idList.add(logisticId);
            portGraphService.removeLogisticFromGraph(logisticId.longValue());
        }

        baseMapper.deleteBatchIds(idList);
        redisLogisticService.clearAllLogistics();
        log.info("Logistics {} deleted from MySQL and Neo4j, Redis cache cleared", ids);
    }

    @Override
    @Transactional
    public void update(LogisticDTO logisticDTO) {
        Logistic logistic = new Logistic();
        BeanUtils.copyProperties(logisticDTO, logistic);
        logistic.setUpdateTime(LocalDateTime.now());
        baseMapper.updateById(logistic);

        portGraphService.updateLogisticInGraph(logistic);
        redisLogisticService.clearAllLogistics();
        log.info("Logistic {} updated in MySQL and Neo4j, Redis cache cleared", logistic.getId());
    }

    public List<Logistic> selectFromCacheOrDb() {
        try {
            UserLoginDTO currentUser = UserHolder.getUser();
            Integer userId = currentUser != null ? currentUser.getId() : null;
            
            List<Integer> userOrderIds;
            if (userId != null) {
                QueryWrapper<Order> orderQuery = new QueryWrapper<>();
                orderQuery.eq("user_id", userId);
                List<Order> userOrders = orderMapper.selectList(orderQuery);
                userOrderIds = userOrders.stream()
                        .map(Order::getId)
                        .toList();
                log.info("User {} has {} orders", userId, userOrderIds.size());
            } else {
                userOrderIds = new ArrayList<>();
            }

            List<Logistic> cachedLogistics = redisLogisticService.getAllLogistics();
            log.info("Redis cached logistics count: {}", cachedLogistics.size());
            
            List<Logistic> userLogistics;
            if (!cachedLogistics.isEmpty()) {
                if (userId != null && !userOrderIds.isEmpty()) {
                    userLogistics = cachedLogistics.stream()
                            .filter(logistic -> userOrderIds.contains(logistic.getOrderId()))
                            .toList();
                    log.info("Filtered user {} logistics from Redis: {}", userId, userLogistics.size());
                } else {
                    userLogistics = cachedLogistics;
                }
            } else {
                log.info("No logistics in Redis cache, fetching from MySQL");
                QueryWrapper<Logistic> q = new QueryWrapper<>();
                if (userId != null && !userOrderIds.isEmpty()) {
                    q.in("order_id", userOrderIds);
                }
                userLogistics = baseMapper.selectList(q);
                log.info("MySQL logistics count for user {}: {}", userId, userLogistics.size());
                
                for (Logistic logistic : userLogistics) {
                    redisLogisticService.addLogistic(logistic);
                }
            }
            return userLogistics;
        } catch (Exception e) {
            log.error("Error in selectFromCacheOrDb: ", e);
            throw e;
        }
    }
}
