package com.smu.portlogisticsdistributionsystem.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smu.portlogisticsdistributionsystem.common.Result;
import com.smu.portlogisticsdistributionsystem.dto.PortDTO;
import com.smu.portlogisticsdistributionsystem.dto.PortQueryDTO;
import com.smu.portlogisticsdistributionsystem.entity.Port;
import com.smu.portlogisticsdistributionsystem.service.PortService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Api(tags = "港口相关接口")
@RestController
@RequestMapping("/api/port")
@CrossOrigin
public class PortController {
    @Autowired
    PortService portService;
    @GetMapping("/select")
    @ApiOperation("查询港口")
    public Page<Port> select(@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize
    , @RequestParam(required = false) @RequestBody PortQueryDTO portDTO){
        return portService.select(pageNum,pageSize,portDTO);
    }
    @ApiOperation("添加港口")
    @PostMapping("/add")
    public Result add(@RequestBody PortDTO portDTO){
        portService.add(portDTO);
        return Result.success();
    }
    @ApiOperation("删除港口")
    @DeleteMapping("/delete/{ids}")
    public Result delete(@PathVariable String ids){
        portService.delete(ids);
        return Result.success();
    }
    @ApiOperation("修改港口")
    @PutMapping("/update")
    public Result update(@RequestBody PortDTO portDTO){
        portService.update(portDTO);
        return Result.success();
    }
}
