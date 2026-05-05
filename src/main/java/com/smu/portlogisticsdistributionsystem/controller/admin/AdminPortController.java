package com.smu.portlogisticsdistributionsystem.controller.admin;

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

@Api(tags = "管理员港口管理")
@RestController
@RequestMapping("/api/admin/port")
@CrossOrigin
public class AdminPortController {
    @Autowired
    PortService portService;

    @GetMapping("/select")
    @ApiOperation("查询所有港口")
    public Result<Page<Port>> select(@RequestParam(defaultValue = "1") int pageNum,
                                      @RequestParam(defaultValue = "10") int pageSize,
                                      PortQueryDTO portQueryDTO) {
        Page<Port> page = portService.select(pageNum, pageSize, portQueryDTO);
        return Result.success(page);
    }

    @ApiOperation("添加港口")
    @PostMapping("/add")
    public Result add(@RequestBody PortDTO portDTO) {
        portService.add(portDTO);
        return Result.success();
    }

    @ApiOperation("删除港口")
    @DeleteMapping("/delete/{ids}")
    public Result delete(@PathVariable String ids) {
        portService.delete(ids);
        return Result.success();
    }

    @ApiOperation("修改港口")
    @PutMapping("/update")
    public Result update(@RequestBody PortDTO portDTO) {
        portService.update(portDTO);
        return Result.success();
    }
}
