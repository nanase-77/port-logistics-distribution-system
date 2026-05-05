package com.smu.portlogisticsdistributionsystem.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smu.portlogisticsdistributionsystem.common.Result;
import com.smu.portlogisticsdistributionsystem.dto.UserDTO;
import com.smu.portlogisticsdistributionsystem.entity.User;
import com.smu.portlogisticsdistributionsystem.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "管理员用户管理")
@RestController
@RequestMapping("/api/admin/user")
@CrossOrigin
public class AdminUserController {
    @Autowired
    UserService userService;

    @GetMapping("/select")
    @ApiOperation("查询所有用户")
    public Result<Page<User>> select(@RequestParam(defaultValue = "1") int pageNum,
                                     @RequestParam(defaultValue = "10") int pageSize) {
        Page<User> page = userService.select(pageNum, pageSize);
        return Result.success(page);
    }

    @ApiOperation("添加用户")
    @PostMapping("/add")
    public Result add(@RequestBody UserDTO userDTO) {
        userService.add(userDTO);
        return Result.success();
    }

    @ApiOperation("删除用户")
    @DeleteMapping("/delete/{ids}")
    public Result delete(@PathVariable String ids) {
        userService.delete(ids);
        return Result.success();
    }

    @ApiOperation("修改用户")
    @PutMapping("/update")
    public Result update(@RequestBody UserDTO userDTO) {
        userService.update(userDTO);
        return Result.success();
    }
}
