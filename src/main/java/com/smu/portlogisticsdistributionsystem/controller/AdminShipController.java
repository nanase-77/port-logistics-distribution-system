package com.smu.portlogisticsdistributionsystem.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.smu.portlogisticsdistributionsystem.common.Result;
import com.smu.portlogisticsdistributionsystem.dto.ShipDTO;
import com.smu.portlogisticsdistributionsystem.dto.ShipQueryDTO;
import com.smu.portlogisticsdistributionsystem.dto.ShipWithCompanyDTO;
import com.smu.portlogisticsdistributionsystem.service.impl.ShipServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "管理员船舶管理接口")
@RestController
@RequestMapping("/api/admin/ship")
@CrossOrigin
public class AdminShipController {
    @Autowired
    ShipServiceImpl shipService;

    @GetMapping("/select")
    @ApiOperation("查询船舶(管理员分页查询)")
    public Result<IPage<ShipWithCompanyDTO>> select(@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize,
                                      ShipQueryDTO shipQueryDTO) {
        return Result.success(shipService.select(pageNum, pageSize, shipQueryDTO));
    }

    @ApiOperation("添加船舶")
    @PostMapping("/add")
    public Result add(@RequestBody ShipDTO shipDTO) {
        shipService.add(shipDTO);
        return Result.success();
    }

    @ApiOperation("删除船舶")
    @DeleteMapping("/delete/{ids}")
    public Result delete(@PathVariable String ids) {
        shipService.delete(ids);
        return Result.success();
    }

    @ApiOperation("修改船舶")
    @PutMapping("/update")
    public Result update(@RequestBody ShipDTO shipDTO) {
        shipService.update(shipDTO);
        return Result.success();
    }
}
