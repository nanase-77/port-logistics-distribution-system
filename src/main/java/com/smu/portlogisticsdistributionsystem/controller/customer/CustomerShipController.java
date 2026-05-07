package com.smu.portlogisticsdistributionsystem.controller.customer;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smu.portlogisticsdistributionsystem.common.Result;
import com.smu.portlogisticsdistributionsystem.service.ShipService;
import com.smu.portlogisticsdistributionsystem.vo.ShipVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "普通用户船舶查看")
@RestController
@RequestMapping("/api/customer/ship")
@CrossOrigin
public class CustomerShipController {
    @Autowired
    ShipService shipService;

    @GetMapping("/select")
    @ApiOperation("查询船舶")
    public Result<Page<ShipVO>> select(@RequestParam(defaultValue = "1") int pageNum,
                                      @RequestParam(defaultValue = "10") int pageSize) {
        Page<ShipVO> page = shipService.select(pageNum, pageSize);
        return Result.success(page);
    }
}
