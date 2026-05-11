package com.smu.portlogisticsdistributionsystem.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smu.portlogisticsdistributionsystem.common.Result;
import com.smu.portlogisticsdistributionsystem.dto.UserDTO;
import com.smu.portlogisticsdistributionsystem.dto.UserQueryDTO;
import com.smu.portlogisticsdistributionsystem.entity.User;
import com.smu.portlogisticsdistributionsystem.service.impl.UserServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "管理员用户管理接口")
@RestController
@RequestMapping("/api/admin/user")
@CrossOrigin
public class AdminUserController {

    @Autowired
    private UserServiceImpl userService;

    @GetMapping("/select")
    @ApiOperation("查询用户(管理员分页查询)")
    public Result<Page<User>> select(@RequestParam(defaultValue = "1") int pageNum,
                                     @RequestParam(defaultValue = "10") int pageSize,
                                     UserQueryDTO userQueryDTO) {
        return Result.success(userService.select(pageNum, pageSize, userQueryDTO));
    }

    @PostMapping("/add")
    @ApiOperation("添加用户")
    public Result add(@RequestBody UserDTO userDTO) {
        userService.add(userDTO);
        return Result.success();
    }

    @DeleteMapping("/delete/{ids}")
    @ApiOperation("删除用户")
    public Result delete(@PathVariable String ids) {
        userService.delete(ids);
        return Result.success();
    }

    @PutMapping("/update")
    @ApiOperation("修改用户")
    public Result update(@RequestBody UserDTO userDTO) {
        userService.update(userDTO);
        return Result.success();
    }
}
