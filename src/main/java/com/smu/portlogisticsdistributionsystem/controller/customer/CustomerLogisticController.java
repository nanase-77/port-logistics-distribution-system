package com.smu.portlogisticsdistributionsystem.controller.customer;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smu.portlogisticsdistributionsystem.common.Result;
import com.smu.portlogisticsdistributionsystem.entity.Logistic;
import com.smu.portlogisticsdistributionsystem.entity.Order;
import com.smu.portlogisticsdistributionsystem.entity.User;
import com.smu.portlogisticsdistributionsystem.service.CustomerCacheService;
import com.smu.portlogisticsdistributionsystem.service.LogisticService;
import com.smu.portlogisticsdistributionsystem.service.OrderService;
import com.smu.portlogisticsdistributionsystem.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "普通用户物流跟踪查看")
@RestController
@RequestMapping("/api/customer/logistic")
@CrossOrigin
public class CustomerLogisticController {
    @Autowired
    LogisticService logisticService;

    @Autowired
    UserService userService;

    @Autowired
    OrderService orderService;

    @Autowired
    CustomerCacheService customerCacheService;

    @GetMapping("/select")
    @ApiOperation("查询我的物流跟踪")
    public Result<Page<Logistic>> select(@RequestParam(defaultValue = "1") int pageNum,
                                         @RequestParam(defaultValue = "10") int pageSize) {
        User currentUser = userService.getCurrentUser();

        Page<Logistic> cachedPage = customerCacheService.getCachedUserLogistics(currentUser.getId());
        if (cachedPage != null) {
            return Result.success(cachedPage);
        }

        LambdaQueryWrapper<Order> orderQuery = new LambdaQueryWrapper<>();
        orderQuery.eq(Order::getUserId, currentUser.getId());
        List<Order> myOrders = orderService.list(orderQuery);
        List<Integer> myOrderIds = myOrders.stream().map(Order::getId).collect(Collectors.toList());
        Page<Logistic> page = logisticService.selectByOrderIds(pageNum, pageSize, myOrderIds);
        customerCacheService.cacheUserLogistics(currentUser.getId(), page);
        return Result.success(page);
    }
}