package com.smu.portlogisticsdistributionsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smu.portlogisticsdistributionsystem.dto.UserDTO;
import com.smu.portlogisticsdistributionsystem.dto.UserLoginDTO;
import com.smu.portlogisticsdistributionsystem.dto.UserRegisterDTO;
import com.smu.portlogisticsdistributionsystem.entity.User;
import com.smu.portlogisticsdistributionsystem.mapper.UserMapper;
import com.smu.portlogisticsdistributionsystem.service.UserService;
import com.smu.portlogisticsdistributionsystem.util.JwtUtil;
import com.smu.portlogisticsdistributionsystem.util.UserHolder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.smu.portlogisticsdistributionsystem.util.RedisConstants;


@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Map<String, Object> login(UserLoginDTO loginDTO) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, loginDTO.getUsername());
        User user = this.getOne(wrapper);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        String role = user.getState() == 1 ? "admin" : "user";
        String token = jwtUtil.generateToken(user.getUsername(), role);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("username", user.getUsername());
        result.put("role", role);
        stringRedisTemplate.opsForHash().putAll(RedisConstants.LOGIN_SESSION+token,result);
        stringRedisTemplate.expire(RedisConstants.LOGIN_SESSION+token,30, TimeUnit.MINUTES);
        return result;
    }

    @Override
    public boolean register(UserRegisterDTO registerDTO) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, registerDTO.getUsername());
        User existingUser = this.getOne(wrapper);

        if (existingUser != null) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setPhone(registerDTO.getPhone());
        user.setCountry(registerDTO.getCountry());
        user.setState(0);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        return this.save(user);
    }

    @Override
    public Page<User> select(int pageNum, int pageSize) {
        Page<User> p = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<User> q = new LambdaQueryWrapper<>();
        return baseMapper.selectPage(p, q);
    }

    @Override
    public void add(UserDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        if (userDTO.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        baseMapper.insert(user);
    }

    @Override
    public void delete(String ids) {
        String[] idArray = ids.split(",");
        List<Integer> idList = new ArrayList<>();
        for (String id : idArray) {
            idList.add(Integer.valueOf(id));
        }
        baseMapper.deleteBatchIds(idList);
    }

    @Override
    public void update(UserDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        } else {
            User oldUser = baseMapper.selectById(userDTO.getId());
            user.setPassword(oldUser.getPassword());
        }
        user.setUpdateTime(LocalDateTime.now());
        baseMapper.updateById(user);
    }

    @Override
    public User getCurrentUser() {
        String username = UserHolder.getUser().getUsername();
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return this.getOne(wrapper);
    }
}
