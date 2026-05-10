package com.smu.portlogisticsdistributionsystem.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smu.portlogisticsdistributionsystem.common.Result;
import com.smu.portlogisticsdistributionsystem.dto.LogisticDTO;
import com.smu.portlogisticsdistributionsystem.dto.LogisticQueryDTO;
import com.smu.portlogisticsdistributionsystem.entity.Logistic;
import com.smu.portlogisticsdistributionsystem.entity.Order;
import com.smu.portlogisticsdistributionsystem.service.CustomerCacheService;
import com.smu.portlogisticsdistributionsystem.service.LogisticService;
import com.smu.portlogisticsdistributionsystem.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Api(tags = "管理员物流跟踪管理")
@RestController
@RequestMapping("/api/admin/logistic")
@CrossOrigin
public class AdminLogisticController {
    @Autowired
    LogisticService logisticService;

    @Autowired
    OrderService orderService;

    @Autowired
    CustomerCacheService customerCacheService;

    @GetMapping("/select")
    @ApiOperation("查询所有物流跟踪")
    public Result<Page<Logistic>> select(@RequestParam(defaultValue = "1") int pageNum,
                                         @RequestParam(defaultValue = "10") int pageSize,
                                         LogisticQueryDTO logisticQueryDTO) {
        Page<Logistic> page = logisticService.select(pageNum, pageSize, logisticQueryDTO);
        return Result.success(page);
    }

    @ApiOperation("添加物流跟踪")
    @PostMapping("/add")
    public Result add(@RequestBody LogisticDTO logisticDTO) {
        logisticService.add(logisticDTO);
        invalidateCacheForOrder(logisticDTO.getOrderId());
        return Result.success();
    }

    @ApiOperation("删除物流跟踪")
    @DeleteMapping("/delete/{ids}")
    public Result delete(@PathVariable String ids) {
        Set<Integer> affectedUserIds = getAffectedUserIds(ids);
        logisticService.delete(ids);
        for (Integer userId : affectedUserIds) {
            customerCacheService.invalidateUserLogistics(userId);
        }
        return Result.success();
    }

    @ApiOperation("修改物流跟踪")
    @PutMapping("/update")
    public Result update(@RequestBody LogisticDTO logisticDTO) {
        Logistic existingLogistic = logisticService.getById(logisticDTO.getId());
        logisticService.update(logisticDTO);
        if (existingLogistic != null) {
            invalidateCacheForOrder(existingLogistic.getOrderId());
        }
        invalidateCacheForOrder(logisticDTO.getOrderId());
        return Result.success();
    }

    private void invalidateCacheForOrder(Integer orderId) {
        if (orderId != null) {
            Order order = orderService.getById(orderId);
            if (order != null && order.getUserId() != null) {
                customerCacheService.invalidateUserLogistics(order.getUserId());
            }
        }
    }

    private Set<Integer> getAffectedUserIds(String ids) {
        Set<Integer> userIds = new HashSet<>();
        String[] idArray = ids.split(",");
        for (String id : idArray) {
            Logistic logistic = logisticService.getById(Integer.parseInt(id.trim()));
            if (logistic != null && logistic.getOrderId() != null) {
                Order order = orderService.getById(logistic.getOrderId());
                if (order != null && order.getUserId() != null) {
                    userIds.add(order.getUserId());
                }
            }
        }
        return userIds;
    }
}