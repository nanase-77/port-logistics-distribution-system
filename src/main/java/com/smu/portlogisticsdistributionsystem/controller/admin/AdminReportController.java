package com.smu.portlogisticsdistributionsystem.controller.admin;

import com.smu.portlogisticsdistributionsystem.common.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

@Api(tags = "管理员数据报表")
@RestController
@RequestMapping("/api/admin/report")
@CrossOrigin
public class AdminReportController {

    @ApiOperation("获取报表数据")
    @GetMapping("/data")
    public Result getReportData() {
        return Result.success();
    }

    @ApiOperation("导出报表")
    @GetMapping("/export")
    public Result exportReport() {
        return Result.success();
    }
}
