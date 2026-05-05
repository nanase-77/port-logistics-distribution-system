package com.smu.portlogisticsdistributionsystem.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smu.portlogisticsdistributionsystem.common.Result;
import com.smu.portlogisticsdistributionsystem.dto.ShipDTO;
import com.smu.portlogisticsdistributionsystem.entity.Ship;
import com.smu.portlogisticsdistributionsystem.service.ShipService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "管理员船舶管理")
@RestController
@RequestMapping("/api/admin/ship")
@CrossOrigin
public class AdminShipController {
    @Autowired
    ShipService shipService;

    @GetMapping("/select")
    @ApiOperation("查询所有船舶")
    public Result<Page<Ship>> select(@RequestParam(defaultValue = "1") int pageNum,
                                     @RequestParam(defaultValue = "10") int pageSize) {
        Page<Ship> page = shipService.select(pageNum, pageSize);
        return Result.success(page);
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
