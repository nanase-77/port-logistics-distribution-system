package com.smu.portlogisticsdistributionsystem.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smu.portlogisticsdistributionsystem.common.Result;
import com.smu.portlogisticsdistributionsystem.dto.PortQueryDTO;
import com.smu.portlogisticsdistributionsystem.entity.Port;
import com.smu.portlogisticsdistributionsystem.service.impl.PortServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "客户港口查询接口")
@RestController
@RequestMapping("/api/customer/port")
@CrossOrigin
public class CustomerPortController {
    @Autowired
    PortServiceImpl portService;

    @GetMapping("/select")
    @ApiOperation("查询港口(客户分页查询)")
    public Result<Page<Port>> select(@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize,
                             PortQueryDTO portQueryDTO) {
        return Result.success(portService.select(pageNum, pageSize, portQueryDTO));
    }
}
