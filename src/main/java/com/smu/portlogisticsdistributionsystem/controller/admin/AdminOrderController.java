package com.smu.portlogisticsdistributionsystem.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smu.portlogisticsdistributionsystem.common.Result;
import com.smu.portlogisticsdistributionsystem.dto.OrderDTO;
import com.smu.portlogisticsdistributionsystem.dto.OrderQueryDTO;
import com.smu.portlogisticsdistributionsystem.entity.Order;
import com.smu.portlogisticsdistributionsystem.entity.User;
import com.smu.portlogisticsdistributionsystem.service.CustomerCacheService;
import com.smu.portlogisticsdistributionsystem.service.OrderService;
import com.smu.portlogisticsdistributionsystem.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Api(tags = "管理员订单管理")
@RestController
@RequestMapping("/api/admin/order")
@CrossOrigin
public class AdminOrderController {
    @Autowired
    OrderService orderService;

    @Autowired
    UserService userService;

    @Autowired
    CustomerCacheService customerCacheService;

    @GetMapping("/select")
    @ApiOperation("查询所有订单")
    public Result<Page<Order>> select(@RequestParam(defaultValue = "1") int pageNum,
                                     @RequestParam(defaultValue = "10") int pageSize,
                                     OrderQueryDTO orderQueryDTO) {
        Page<Order> page = orderService.select(pageNum, pageSize, orderQueryDTO);
        return Result.success(page);
    }

    @GetMapping("/count")
    public Result<Integer> getOrderCount() {
        return Result.success(orderService.getOrderCount());
    }

    @ApiOperation("添加订单")
    @PostMapping("/add")
    public Result add(@RequestBody OrderDTO orderDTO) {
        orderService.add(orderDTO);
        if (orderDTO.getUserId() != null) {
            customerCacheService.invalidateUserOrders(orderDTO.getUserId());
            customerCacheService.invalidateUserLogistics(orderDTO.getUserId());
        }
        return Result.success();
    }

    @ApiOperation("删除订单")
    @DeleteMapping("/delete/{ids}")
    public Result delete(@PathVariable String ids) {
        List<Integer> affectedUserIds = getAffectedUserIds(ids);
        orderService.delete(ids);
        for (Integer userId : affectedUserIds) {
            customerCacheService.invalidateUserOrders(userId);
            customerCacheService.invalidateUserLogistics(userId);
        }
        return Result.success();
    }

    @ApiOperation("修改订单")
    @PutMapping("/update")
    public Result update(@RequestBody OrderDTO orderDTO) {
        Order existingOrder = orderService.getById(orderDTO.getId());
        orderService.update(orderDTO);
        if (existingOrder != null && existingOrder.getUserId() != null) {
            customerCacheService.invalidateUserOrders(existingOrder.getUserId());
            customerCacheService.invalidateUserLogistics(existingOrder.getUserId());
        }
        if (orderDTO.getUserId() != null && !orderDTO.getUserId().equals(existingOrder != null ? existingOrder.getUserId() : null)) {
            customerCacheService.invalidateUserOrders(orderDTO.getUserId());
            customerCacheService.invalidateUserLogistics(orderDTO.getUserId());
        }
        return Result.success();
    }

    private List<Integer> getAffectedUserIds(String ids) {
        List<Integer> userIds = new ArrayList<>();
        String[] idArray = ids.split(",");
        for (String id : idArray) {
            Order order = orderService.getById(Integer.parseInt(id.trim()));
            if (order != null && order.getUserId() != null) {
                userIds.add(order.getUserId());
            }
        }
        return userIds;
    }
}