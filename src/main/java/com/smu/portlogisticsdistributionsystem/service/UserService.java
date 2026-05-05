package com.smu.portlogisticsdistributionsystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.smu.portlogisticsdistributionsystem.dto.UserDTO;
import com.smu.portlogisticsdistributionsystem.dto.UserLoginDTO;
import com.smu.portlogisticsdistributionsystem.dto.UserRegisterDTO;
import com.smu.portlogisticsdistributionsystem.entity.User;

import java.util.Map;

public interface UserService extends IService<User> {
    Map<String, Object> login(UserLoginDTO loginDTO);
    boolean register(UserRegisterDTO registerDTO);
    Page<User> select(int pageNum, int pageSize);
    void add(UserDTO userDTO);
    void delete(String ids);
    void update(UserDTO userDTO);
    User getCurrentUser();
}
