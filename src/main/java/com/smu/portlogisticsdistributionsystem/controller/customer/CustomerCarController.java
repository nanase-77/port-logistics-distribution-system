package com.smu.portlogisticsdistributionsystem.controller.customer;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smu.portlogisticsdistributionsystem.common.Result;
import com.smu.portlogisticsdistributionsystem.dto.CarQueryDTO;
import com.smu.portlogisticsdistributionsystem.entity.Car;
import com.smu.portlogisticsdistributionsystem.service.CarService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "普通用户车辆查看")
@RestController
@RequestMapping("/api/customer/car")
@CrossOrigin
public class CustomerCarController {
    @Autowired
    CarService carService;

    @GetMapping("/select")
    @ApiOperation("查询车辆")
    public Result<Page<Car>> select(@RequestParam(defaultValue = "1") int pageNum,
                                     @RequestParam(defaultValue = "10") int pageSize,
                                     CarQueryDTO carQueryDTO) {
        Page<Car> page = carService.select(pageNum, pageSize, carQueryDTO);
        return Result.success(page);
    }
}
