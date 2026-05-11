package com.smu.portlogisticsdistributionsystem.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smu.portlogisticsdistributionsystem.common.Result;
import com.smu.portlogisticsdistributionsystem.dto.LogisticDTO;
import com.smu.portlogisticsdistributionsystem.dto.LogisticQueryDTO;
import com.smu.portlogisticsdistributionsystem.entity.Logistic;
import com.smu.portlogisticsdistributionsystem.service.impl.LogisticServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "物流跟踪相关接口")
@RestController
@RequestMapping("/api/logistic")
@CrossOrigin
public class LogisticController {
    @Autowired
    LogisticServiceImpl logisticService;

    @GetMapping("/select")
    @ApiOperation("查询物流跟踪(管理员分页查询)")
    public Result<Page<Logistic>> select(@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize,
                               LogisticQueryDTO logisticQueryDTO) {
        return Result.success(logisticService.select(pageNum, pageSize, logisticQueryDTO));
    }

    @GetMapping("/list")
    @ApiOperation("查询物流跟踪列表(用户查询，缓存优先)")
    public Result<List<Logistic>> list() {
        return Result.success(logisticService.selectFromCacheOrDb());
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
