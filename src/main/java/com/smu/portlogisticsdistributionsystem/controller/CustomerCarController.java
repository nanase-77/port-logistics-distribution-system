package com.smu.portlogisticsdistributionsystem.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.smu.portlogisticsdistributionsystem.common.Result;
import com.smu.portlogisticsdistributionsystem.dto.CarQueryDTO;
import com.smu.portlogisticsdistributionsystem.dto.CarWithPortDTO;
import com.smu.portlogisticsdistributionsystem.entity.Car;
import com.smu.portlogisticsdistributionsystem.service.impl.CarServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "客户车辆查询接口")
@RestController
@RequestMapping("/api/customer/car")
@CrossOrigin
public class CustomerCarController {
    @Autowired
    CarServiceImpl carService;

    @GetMapping("/select")
    @ApiOperation("查询车辆(客户分页查询，连表查询港口名称)")
    public Result<IPage<CarWithPortDTO>> select(@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize,
                           CarQueryDTO carQueryDTO) {
        return Result.success(carService.selectWithPort(pageNum, pageSize));
    }
}
