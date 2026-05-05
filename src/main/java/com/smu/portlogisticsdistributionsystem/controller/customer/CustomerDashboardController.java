package com.smu.portlogisticsdistributionsystem.controller.customer;

import com.smu.portlogisticsdistributionsystem.common.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

@Api(tags = "普通用户仪表盘")
@RestController
@RequestMapping("/api/customer/dashboard")
@CrossOrigin
public class CustomerDashboardController {

    @ApiOperation("获取仪表盘数据")
    @GetMapping("/data")
    public Result getDashboardData() {
        return Result.success();
    }
}
