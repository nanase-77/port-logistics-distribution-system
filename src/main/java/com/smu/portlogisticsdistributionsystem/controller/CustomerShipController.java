package com.smu.portlogisticsdistributionsystem.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.smu.portlogisticsdistributionsystem.common.Result;
import com.smu.portlogisticsdistributionsystem.dto.ShipQueryDTO;
import com.smu.portlogisticsdistributionsystem.dto.ShipWithCompanyDTO;
import com.smu.portlogisticsdistributionsystem.service.impl.ShipServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "客户船舶查询接口")
@RestController
@RequestMapping("/api/customer/ship")
@CrossOrigin
public class CustomerShipController {
    @Autowired
    ShipServiceImpl shipService;

    @GetMapping("/select")
    @ApiOperation("查询船舶(客户分页查询)")
    public Result<IPage<ShipWithCompanyDTO>> select(@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize,
                                      ShipQueryDTO shipQueryDTO) {
        return Result.success(shipService.select(pageNum, pageSize, shipQueryDTO));
    }
}
