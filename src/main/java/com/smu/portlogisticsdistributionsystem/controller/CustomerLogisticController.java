package com.smu.portlogisticsdistributionsystem.controller;

import com.smu.portlogisticsdistributionsystem.common.Result;
import com.smu.portlogisticsdistributionsystem.entity.Logistic;
import com.smu.portlogisticsdistributionsystem.service.impl.LogisticServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "客户物流跟踪接口")
@RestController
@RequestMapping("/api/customer/logistic")
@CrossOrigin
public class CustomerLogisticController {
    @Autowired
    LogisticServiceImpl logisticService;

    @GetMapping("/list")
    @ApiOperation("查询物流跟踪列表(客户查询，缓存优先)")
    public Result<List<Logistic>> list() {
        return Result.success(logisticService.selectFromCacheOrDb());
    }
}
