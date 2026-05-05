package com.smu.portlogisticsdistributionsystem.controller.admin;

import com.smu.portlogisticsdistributionsystem.common.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

@Api(tags = "管理员调度管理")
@RestController
@RequestMapping("/api/admin/dispatch")
@CrossOrigin
public class AdminDispatchController {

    @ApiOperation("调度分配")
    @PostMapping("/assign")
    public Result assign(@RequestBody Object data) {
        return Result.success();
    }

    @ApiOperation("获取调度列表")
    @GetMapping("/list")
    public Result list() {
        return Result.success();
    }
}
