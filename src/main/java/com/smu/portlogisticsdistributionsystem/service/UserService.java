package com.smu.portlogisticsdistributionsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smu.portlogisticsdistributionsystem.dto.UserLoginDTO;
import com.smu.portlogisticsdistributionsystem.dto.UserRegisterDTO;
import com.smu.portlogisticsdistributionsystem.entity.User;

import java.util.Map;

public interface UserService extends IService<User> {
    Map<String, Object> login(UserLoginDTO loginDTO);
    boolean register(UserRegisterDTO registerDTO);
}
