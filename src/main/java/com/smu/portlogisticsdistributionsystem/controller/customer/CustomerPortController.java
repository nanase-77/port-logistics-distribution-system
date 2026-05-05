package com.smu.portlogisticsdistributionsystem.controller.customer;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smu.portlogisticsdistributionsystem.common.Result;
import com.smu.portlogisticsdistributionsystem.dto.PortQueryDTO;
import com.smu.portlogisticsdistributionsystem.entity.Port;
import com.smu.portlogisticsdistributionsystem.service.PortService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "普通用户港口查看")
@RestController
@RequestMapping("/api/customer/port")
@CrossOrigin
public class CustomerPortController {
    @Autowired
    PortService portService;

    @GetMapping("/select")
    @ApiOperation("查询港口")
    public Result<Page<Port>> select(@RequestParam(defaultValue = "1") int pageNum,
                                      @RequestParam(defaultValue = "10") int pageSize,
                                      PortQueryDTO portQueryDTO) {
        Page<Port> page = portService.select(pageNum, pageSize, portQueryDTO);
        return Result.success(page);
    }
}
