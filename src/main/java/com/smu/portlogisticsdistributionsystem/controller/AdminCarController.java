package com.smu.portlogisticsdistributionsystem.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smu.portlogisticsdistributionsystem.common.Result;
import com.smu.portlogisticsdistributionsystem.dto.CarDTO;
import com.smu.portlogisticsdistributionsystem.dto.CarQueryDTO;
import com.smu.portlogisticsdistributionsystem.entity.Car;
import com.smu.portlogisticsdistributionsystem.service.impl.CarServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "管理员车辆管理接口")
@RestController
@RequestMapping("/api/admin/car")
@CrossOrigin
public class AdminCarController {

    @Autowired
    private CarServiceImpl carService;

    @GetMapping("/select")
    @ApiOperation("查询车辆(管理员分页查询)")
    public Result<Page<Car>> select(@RequestParam(defaultValue = "1") int pageNum,
                                  @RequestParam(defaultValue = "10") int pageSize,
                                  CarQueryDTO carQueryDTO) {
        return Result.success(carService.select(pageNum, pageSize, carQueryDTO));
    }

    @PostMapping("/add")
    @ApiOperation("添加车辆")
    public Result add(@RequestBody CarDTO carDTO) {
        carService.add(carDTO);
        return Result.success();
    }

    @DeleteMapping("/delete/{ids}")
    @ApiOperation("删除车辆")
    public Result delete(@PathVariable String ids) {
        carService.delete(ids);
        return Result.success();
    }

    @PutMapping("/update")
    @ApiOperation("修改车辆")
    public Result update(@RequestBody CarDTO carDTO) {
        carService.update(carDTO);
        return Result.success();
    }
}
