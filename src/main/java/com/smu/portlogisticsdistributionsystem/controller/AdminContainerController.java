package com.smu.portlogisticsdistributionsystem.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smu.portlogisticsdistributionsystem.common.Result;
import com.smu.portlogisticsdistributionsystem.dto.ContainerDTO;
import com.smu.portlogisticsdistributionsystem.dto.ContainerQueryDTO;
import com.smu.portlogisticsdistributionsystem.dto.ContainerWithCompanyDTO;
import com.smu.portlogisticsdistributionsystem.entity.Container;
import com.smu.portlogisticsdistributionsystem.service.impl.ContainerServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "管理员集装箱管理接口")
@RestController
@RequestMapping("/api/admin/container")
@CrossOrigin
public class AdminContainerController {

    @Autowired
    private ContainerServiceImpl containerService;

    @GetMapping("/select")
    @ApiOperation("查询集装箱(管理员分页查询，连表查询companyName)")
    public Result<IPage<ContainerWithCompanyDTO>> select(@RequestParam(defaultValue = "1") int pageNum,
                                        @RequestParam(defaultValue = "10") int pageSize,
                                        ContainerQueryDTO containerQueryDTO) {
        return Result.success(containerService.selectWithCompany(pageNum, pageSize));
    }

    @PostMapping("/add")
    @ApiOperation("添加集装箱")
    public Result add(@RequestBody ContainerDTO containerDTO) {
        containerService.add(containerDTO);
        return Result.success();
    }

    @DeleteMapping("/delete/{ids}")
    @ApiOperation("删除集装箱")
    public Result delete(@PathVariable String ids) {
        containerService.delete(ids);
        return Result.success();
    }

    @PutMapping("/update")
    @ApiOperation("修改集装箱")
    public Result update(@RequestBody ContainerDTO containerDTO) {
        containerService.update(containerDTO);
        return Result.success();
    }
}
