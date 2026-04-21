package com.smu.portlogisticsdistributionsystem.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smu.portlogisticsdistributionsystem.common.Result;
import com.smu.portlogisticsdistributionsystem.dto.ContainerDTO;
import com.smu.portlogisticsdistributionsystem.dto.ContainerQueryDTO;
import com.smu.portlogisticsdistributionsystem.entity.Container;
import com.smu.portlogisticsdistributionsystem.service.ContainerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "集装箱相关接口")
@RestController
@RequestMapping("/api/container")
@CrossOrigin
public class ContainerController {
    @Autowired
    ContainerService containerService;

    @GetMapping("/select")
    @ApiOperation("查询集装箱")
    public Page<Container> select(@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize,
                               @RequestParam(required = false) @RequestBody ContainerQueryDTO containerQueryDTO) {
        return containerService.select(pageNum, pageSize, containerQueryDTO);
    }

    @ApiOperation("添加集装箱")
    @PostMapping("/add")
    public Result add(@RequestBody ContainerDTO containerDTO) {
        containerService.add(containerDTO);
        return Result.success();
    }

    @ApiOperation("删除集装箱")
    @DeleteMapping("/delete/{ids}")
    public Result delete(@PathVariable String ids) {
        containerService.delete(ids);
        return Result.success();
    }

    @ApiOperation("修改集装箱")
    @PutMapping("/update")
    public Result update(@RequestBody ContainerDTO containerDTO) {
        containerService.update(containerDTO);
        return Result.success();
    }
}