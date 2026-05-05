package com.smu.portlogisticsdistributionsystem.controller.customer;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smu.portlogisticsdistributionsystem.common.Result;
import com.smu.portlogisticsdistributionsystem.dto.CompanyQueryDTO;
import com.smu.portlogisticsdistributionsystem.entity.Company;
import com.smu.portlogisticsdistributionsystem.service.CompanyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "普通用户公司查看")
@RestController
@RequestMapping("/api/customer/company")
@CrossOrigin
public class CustomerCompanyController {
    @Autowired
    CompanyService companyService;

    @GetMapping("/select")
    @ApiOperation("查询公司")
    public Result<Page<Company>> select(@RequestParam(defaultValue = "1") int pageNum,
                                        @RequestParam(defaultValue = "10") int pageSize,
                                        CompanyQueryDTO companyQueryDTO) {
        Page<Company> page = companyService.select(pageNum, pageSize, companyQueryDTO);
        return Result.success(page);
    }
}
