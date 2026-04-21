package com.smu.portlogisticsdistributionsystem.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smu.portlogisticsdistributionsystem.common.Result;
import com.smu.portlogisticsdistributionsystem.dto.LogisticDTO;
import com.smu.portlogisticsdistributionsystem.dto.LogisticQueryDTO;
import com.smu.portlogisticsdistributionsystem.entity.Logistic;
import com.smu.portlogisticsdistributionsystem.service.LogisticService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "物流跟踪相关接口")
@RestController
@RequestMapping("/api/logistic")
@CrossOrigin
public class LogisticController {
    @Autowired
    LogisticService logisticService;

    @GetMapping("/select")
    @ApiOperation("查询物流跟踪")
    public Page<Logistic> select(@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize,
                               @RequestParam(required = false) @RequestBody LogisticQueryDTO logisticQueryDTO) {
        return logisticService.select(pageNum, pageSize, logisticQueryDTO);
    }

    @ApiOperation("添加物流跟踪")
    @PostMapping("/add")
    public Result add(@RequestBody LogisticDTO logisticDTO) {
        logisticService.add(logisticDTO);
        return Result.success();
    }

    @ApiOperation("删除物流跟踪")
    @DeleteMapping("/delete/{ids}")
    public Result delete(@PathVariable String ids) {
        logisticService.delete(ids);
        return Result.success();
    }

    @ApiOperation("修改物流跟踪")
    @PutMapping("/update")
    public Result update(@RequestBody LogisticDTO logisticDTO) {
        logisticService.update(logisticDTO);
        return Result.success();
    }
}