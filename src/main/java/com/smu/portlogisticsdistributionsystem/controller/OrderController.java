package com.smu.portlogisticsdistributionsystem.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smu.portlogisticsdistributionsystem.common.Result;
import com.smu.portlogisticsdistributionsystem.dto.OrderDTO;
import com.smu.portlogisticsdistributionsystem.dto.OrderQueryDTO;
import com.smu.portlogisticsdistributionsystem.entity.Order;
import com.smu.portlogisticsdistributionsystem.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "订单相关接口")
@RestController
@RequestMapping("/api/order")
@CrossOrigin
public class OrderController {
    @Autowired
    OrderService orderService;

    @GetMapping("/select")
    @ApiOperation("查询订单")
    public Result<Page<Order>> select(@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize,
                               OrderQueryDTO orderQueryDTO) {
        return Result.success(orderService.select(pageNum, pageSize, orderQueryDTO));
    }

    @ApiOperation("添加订单")
    @PostMapping("/add")
    public Result add(@RequestBody OrderDTO orderDTO) {
        orderService.add(orderDTO);
        return Result.success();
    }

    @ApiOperation("删除订单")
    @DeleteMapping("/delete/{ids}")
    public Result delete(@PathVariable String ids) {
        orderService.delete(ids);
        return Result.success();
    }

    @ApiOperation("修改订单")
    @PutMapping("/update")
    public Result update(@RequestBody OrderDTO orderDTO) {
        orderService.update(orderDTO);
        return Result.success();
    }
}