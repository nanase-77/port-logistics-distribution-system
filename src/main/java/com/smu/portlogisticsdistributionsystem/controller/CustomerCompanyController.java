package com.smu.portlogisticsdistributionsystem.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smu.portlogisticsdistributionsystem.common.Result;
import com.smu.portlogisticsdistributionsystem.dto.CompanyQueryDTO;
import com.smu.portlogisticsdistributionsystem.entity.Company;
import com.smu.portlogisticsdistributionsystem.service.impl.CompanyServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "客户公司查询接口")
@RestController
@RequestMapping("/customer/company")
@CrossOrigin
public class CustomerCompanyController {
    @Autowired
    CompanyServiceImpl companyService;

    @GetMapping("/select")
    @ApiOperation("查询公司(客户分页查询)")
    public Result<Page<Company>> select(@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize,
                                CompanyQueryDTO companyQueryDTO) {
        return Result.success(companyService.select(pageNum, pageSize, companyQueryDTO));
    }
}
