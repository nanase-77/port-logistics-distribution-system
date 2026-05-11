package com.smu.portlogisticsdistributionsystem.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smu.portlogisticsdistributionsystem.common.Result;
import com.smu.portlogisticsdistributionsystem.dto.CompanyDTO;
import com.smu.portlogisticsdistributionsystem.dto.CompanyQueryDTO;
import com.smu.portlogisticsdistributionsystem.entity.Company;
import com.smu.portlogisticsdistributionsystem.service.impl.CompanyServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "管理员公司管理接口")
@RestController
@RequestMapping("/api/admin/company")
@CrossOrigin
public class AdminCompanyController {
    @Autowired
    CompanyServiceImpl companyService;

    @GetMapping("/select")
    @ApiOperation("查询公司(管理员分页查询)")
    public Result<Page<Company>> select(@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize,
                                CompanyQueryDTO companyQueryDTO) {
        return Result.success(companyService.select(pageNum, pageSize, companyQueryDTO));
    }

    @ApiOperation("添加公司")
    @PostMapping("/add")
    public Result add(@RequestBody CompanyDTO companyDTO) {
        companyService.add(companyDTO);
        return Result.success();
    }

    @ApiOperation("删除公司")
    @DeleteMapping("/delete/{ids}")
    public Result delete(@PathVariable String ids) {
        companyService.delete(ids);
        return Result.success();
    }

    @ApiOperation("修改公司")
    @PutMapping("/update")
    public Result update(@RequestBody CompanyDTO companyDTO) {
        companyService.update(companyDTO);
        return Result.success();
    }
}
