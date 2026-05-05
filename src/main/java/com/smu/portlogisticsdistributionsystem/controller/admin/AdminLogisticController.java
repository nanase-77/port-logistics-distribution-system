package com.smu.portlogisticsdistributionsystem.controller.admin;

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

@Api(tags = "管理员物流跟踪管理")
@RestController
@RequestMapping("/api/admin/logistic")
@CrossOrigin
public class AdminLogisticController {
    @Autowired
    LogisticService logisticService;

    @GetMapping("/select")
    @ApiOperation("查询所有物流跟踪")
    public Result<Page<Logistic>> select(@RequestParam(defaultValue = "1") int pageNum,
                                          @RequestParam(defaultValue = "10") int pageSize,
                                          LogisticQueryDTO logisticQueryDTO) {
        Page<Logistic> page = logisticService.select(pageNum, pageSize, logisticQueryDTO);
        return Result.success(page);
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
