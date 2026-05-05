package com.smu.portlogisticsdistributionsystem.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smu.portlogisticsdistributionsystem.common.Result;
import com.smu.portlogisticsdistributionsystem.dto.CarDTO;
import com.smu.portlogisticsdistributionsystem.dto.CarQueryDTO;
import com.smu.portlogisticsdistributionsystem.entity.Car;
import com.smu.portlogisticsdistributionsystem.service.CarService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "管理员车辆管理")
@RestController
@RequestMapping("/api/admin/car")
@CrossOrigin
public class AdminCarController {
    @Autowired
    CarService carService;

    @GetMapping("/select")
    @ApiOperation("查询所有车辆")
    public Result<Page<Car>> select(@RequestParam(defaultValue = "1") int pageNum,
                                     @RequestParam(defaultValue = "10") int pageSize,
                                     CarQueryDTO carQueryDTO) {
        Page<Car> page = carService.select(pageNum, pageSize, carQueryDTO);
        return Result.success(page);
    }

    @ApiOperation("添加车辆")
    @PostMapping("/add")
    public Result add(@RequestBody CarDTO carDTO) {
        carService.add(carDTO);
        return Result.success();
    }

    @ApiOperation("删除车辆")
    @DeleteMapping("/delete/{ids}")
    public Result delete(@PathVariable String ids) {
        carService.delete(ids);
        return Result.success();
    }

    @ApiOperation("修改车辆")
    @PutMapping("/update")
    public Result update(@RequestBody CarDTO carDTO) {
        carService.update(carDTO);
        return Result.success();
    }
}
