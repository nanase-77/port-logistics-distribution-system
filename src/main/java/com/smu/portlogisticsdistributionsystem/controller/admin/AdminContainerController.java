package com.smu.portlogisticsdistributionsystem.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smu.portlogisticsdistributionsystem.common.Result;
import com.smu.portlogisticsdistributionsystem.dto.ContainerDTO;
import com.smu.portlogisticsdistributionsystem.service.ContainerService;
import com.smu.portlogisticsdistributionsystem.vo.ContainerVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "管理员集装箱管理")
@RestController
@RequestMapping("/api/admin/container")
@CrossOrigin
public class AdminContainerController {
    @Autowired
    ContainerService containerService;

    @GetMapping("/select")
    @ApiOperation("查询所有集装箱")
    public Result<Page<ContainerVO>> select(@RequestParam(defaultValue = "1") int pageNum,
                                           @RequestParam(defaultValue = "10") int pageSize) {
        Page<ContainerVO> page = containerService.select(pageNum, pageSize);
        return Result.success(page);
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