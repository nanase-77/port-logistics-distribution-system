package com.smu.portlogisticsdistributionsystem.controller.customer;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smu.portlogisticsdistributionsystem.common.Result;
import com.smu.portlogisticsdistributionsystem.dto.OrderDTO;
import com.smu.portlogisticsdistributionsystem.entity.Order;
import com.smu.portlogisticsdistributionsystem.entity.User;
import com.smu.portlogisticsdistributionsystem.service.CustomerCacheService;
import com.smu.portlogisticsdistributionsystem.service.OrderService;
import com.smu.portlogisticsdistributionsystem.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "普通用户订单管理")
@RestController
@RequestMapping("/api/customer/order")
@CrossOrigin
public class CustomerOrderController {
    @Autowired
    OrderService orderService;

    @Autowired
    UserService userService;

    @Autowired
    CustomerCacheService customerCacheService;

    @GetMapping("/select")
    @ApiOperation("查询我的订单")
    public Result<Page<Order>> select(@RequestParam(defaultValue = "1") int pageNum,
                                     @RequestParam(defaultValue = "10") int pageSize) {
        User currentUser = userService.getCurrentUser();

        Page<Order> cachedPage = customerCacheService.getCachedUserOrders(currentUser.getId());
        if (cachedPage != null) {
            return Result.success(cachedPage);
        }

        Page<Order> page = orderService.selectByUserId(pageNum, pageSize, currentUser.getId());
        customerCacheService.cacheUserOrders(currentUser.getId(), page);
        return Result.success(page);
    }

    @ApiOperation("添加订单")
    @PostMapping("/add")
    public Result add(@RequestBody OrderDTO orderDTO) {
        User currentUser = userService.getCurrentUser();
        orderService.addByUser(orderDTO, currentUser.getId());
        customerCacheService.invalidateUserOrders(currentUser.getId());
        customerCacheService.invalidateUserLogistics(currentUser.getId());
        return Result.success();
    }
}