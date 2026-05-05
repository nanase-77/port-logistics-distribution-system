package com.smu.portlogisticsdistributionsystem.controller.customer;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smu.portlogisticsdistributionsystem.common.Result;
import com.smu.portlogisticsdistributionsystem.dto.UserDTO;
import com.smu.portlogisticsdistributionsystem.entity.User;
import com.smu.portlogisticsdistributionsystem.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "普通用户个人信息管理")
@RestController
@RequestMapping("/api/customer/user")
@CrossOrigin
public class CustomerUserController {
    @Autowired
    UserService userService;

    @GetMapping("/info")
    @ApiOperation("获取当前用户信息")
    public Result<User> getInfo() {
        User user = userService.getCurrentUser();
        return Result.success(user);
    }

    @ApiOperation("更新个人信息")
    @PutMapping("/update")
    public Result update(@RequestBody UserDTO userDTO) {
        User currentUser = userService.getCurrentUser();
        userDTO.setId(currentUser.getId());
        userService.update(userDTO);
        return Result.success();
    }
}
