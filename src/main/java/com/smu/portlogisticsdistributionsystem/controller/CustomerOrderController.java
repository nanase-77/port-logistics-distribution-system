package com.smu.portlogisticsdistributionsystem.controller;

import com.smu.portlogisticsdistributionsystem.common.Result;
import com.smu.portlogisticsdistributionsystem.dto.OrderDTO;
import com.smu.portlogisticsdistributionsystem.dto.OrderStatsDTO;
import com.smu.portlogisticsdistributionsystem.entity.Order;
import com.smu.portlogisticsdistributionsystem.service.impl.OrderServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "客户订单查询接口")
@RestController
@RequestMapping("/api/customer/order")
@CrossOrigin
public class CustomerOrderController {
    @Autowired
    OrderServiceImpl orderService;

    @GetMapping("/list")
    @ApiOperation("查询订单列表(客户查询，缓存优先)")
    public Result<List<Order>> list() {
        return Result.success(orderService.selectFromCacheOrDb());
    }

    @GetMapping("/stats")
    @ApiOperation("查询订单统计(客户查询)")
    public Result<OrderStatsDTO> stats() {
        return Result.success(orderService.getOrderStats());
    }

    @ApiOperation("添加订单")
    @PostMapping("/add")
    public Result add(@RequestBody OrderDTO orderDTO) {
        orderService.add(orderDTO);
        return Result.success();
    }
}
