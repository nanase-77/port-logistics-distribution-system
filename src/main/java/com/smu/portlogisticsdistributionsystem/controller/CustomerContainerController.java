package com.smu.portlogisticsdistributionsystem.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smu.portlogisticsdistributionsystem.common.Result;
import com.smu.portlogisticsdistributionsystem.dto.ContainerQueryDTO;
import com.smu.portlogisticsdistributionsystem.dto.ContainerWithCompanyDTO;
import com.smu.portlogisticsdistributionsystem.entity.Container;
import com.smu.portlogisticsdistributionsystem.service.impl.ContainerServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "客户集装箱查询接口")
@RestController
@RequestMapping("/api/customer/container")
@CrossOrigin
public class CustomerContainerController {
    @Autowired
    ContainerServiceImpl containerService;

    @GetMapping("/select")
    @ApiOperation("查询集装箱(客户分页查询，连表查询companyName)")
    public Result<IPage<ContainerWithCompanyDTO>> select(@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize,
                                  ContainerQueryDTO containerQueryDTO) {
        return Result.success(containerService.selectWithCompany(pageNum, pageSize));
    }
}
