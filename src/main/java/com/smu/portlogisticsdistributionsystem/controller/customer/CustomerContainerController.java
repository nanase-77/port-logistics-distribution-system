package com.smu.portlogisticsdistributionsystem.controller.customer;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smu.portlogisticsdistributionsystem.common.Result;
import com.smu.portlogisticsdistributionsystem.dto.ContainerQueryDTO;
import com.smu.portlogisticsdistributionsystem.service.ContainerService;
import com.smu.portlogisticsdistributionsystem.vo.ContainerVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "普通用户集装箱查看")
@RestController
@RequestMapping("/api/customer/container")
@CrossOrigin
public class CustomerContainerController {
    @Autowired
    ContainerService containerService;

    @GetMapping("/select")
    @ApiOperation("查询集装箱")
    public Result<Page<ContainerVO>> select(@RequestParam(defaultValue = "1") int pageNum,
                                           @RequestParam(defaultValue = "10") int pageSize,
                                           ContainerQueryDTO containerQueryDTO) {
        Page<ContainerVO> page = containerService.select(pageNum, pageSize, containerQueryDTO);
        return Result.success(page);
    }
}
