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

@Api(tags = "管理员物流跟踪接口")
@RestController
@RequestMapping("/api/admin/logistic")
@CrossOrigin
public class AdminLogisticController {

    @Autowired
    private LogisticServiceImpl logisticService;

    @GetMapping("/select")
    @ApiOperation("查询物流跟踪(管理员分页查询)")
    public Result<Page<Logistic>> select(@RequestParam(defaultValue = "1") int pageNum,
                                       @RequestParam(defaultValue = "10") int pageSize,
                                       LogisticQueryDTO logisticQueryDTO) {
        return Result.success(logisticService.select(pageNum, pageSize, logisticQueryDTO));
    }

    @PostMapping("/add")
    @ApiOperation("添加物流跟踪")
    public Result add(@RequestBody LogisticDTO logisticDTO) {
        logisticService.add(logisticDTO);
        return Result.success();
    }

    @DeleteMapping("/delete/{ids}")
    @ApiOperation("删除物流跟踪")
    public Result delete(@PathVariable String ids) {
        logisticService.delete(ids);
        return Result.success();
    }

    @PutMapping("/update")
    @ApiOperation("修改物流跟踪")
    public Result update(@RequestBody LogisticDTO logisticDTO) {
        logisticService.update(logisticDTO);
        return Result.success();
    }
}
