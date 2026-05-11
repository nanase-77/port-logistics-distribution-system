package com.smu.portlogisticsdistributionsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smu.portlogisticsdistributionsystem.dto.UserDTO;
import com.smu.portlogisticsdistributionsystem.dto.UserLoginDTO;
import com.smu.portlogisticsdistributionsystem.dto.UserQueryDTO;
import com.smu.portlogisticsdistributionsystem.dto.UserRegisterDTO;
import com.smu.portlogisticsdistributionsystem.entity.User;
import com.smu.portlogisticsdistributionsystem.mapper.UserMapper;
import com.smu.portlogisticsdistributionsystem.service.UserService;
import com.smu.portlogisticsdistributionsystem.util.JwtUtil;
import com.smu.portlogisticsdistributionsystem.util.RedisConstants;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
        Map<String, Object> ress = new HashMap<>();
        ress.put("id",String.valueOf(user.getId()));
        ress.put("state",String.valueOf(user.getState()));
        ress.put("name",user.getUsername());
        stringRedisTemplate.opsForHash().putAll(RedisConstants.LOGIN_SESSION + token, ress);
        stringRedisTemplate.expire(RedisConstants.LOGIN_SESSION + token, 30, TimeUnit.MINUTES);
        
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
    public void logout(String token) {
        stringRedisTemplate.delete(RedisConstants.LOGIN_SESSION + token);
    }

    public Page<User> select(int pageNum, int pageSize, UserQueryDTO userQueryDTO) {
        Page<User> p = new Page<>(pageNum, pageSize);
        QueryWrapper<User> q = new QueryWrapper<>();
        if (StringUtils.hasText(userQueryDTO.getUsername())) {
            q.like("username", userQueryDTO.getUsername());
        }
        if (StringUtils.hasText(userQueryDTO.getPhone())) {
            q.like("phone", userQueryDTO.getPhone());
        }
        if (userQueryDTO.getState() != null) {
            q.eq("state", userQueryDTO.getState());
        }
        return baseMapper.selectPage(p, q);
    }

    public void add(UserDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        if (StringUtils.hasText(userDTO.getPassword())) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        baseMapper.insert(user);
    }

    public void delete(String ids) {
        String[] idArray = ids.split(",");
        List<Integer> idList = java.util.Arrays.stream(idArray)
                .map(Integer::valueOf)
                .toList();
        baseMapper.deleteBatchIds(idList);
    }

    public void update(UserDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        if (StringUtils.hasText(userDTO.getPassword())) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        user.setUpdateTime(LocalDateTime.now());
        baseMapper.updateById(user);
    }
}
