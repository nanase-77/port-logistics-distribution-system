package com.smu.portlogisticsdistributionsystem.interceptor;

import cn.hutool.core.bean.BeanUtil;
import com.github.xiaoymin.knife4j.core.util.StrUtil;
import com.smu.portlogisticsdistributionsystem.dto.UserLoginDTO;
import com.smu.portlogisticsdistributionsystem.util.JwtUtil;
import com.smu.portlogisticsdistributionsystem.util.RedisConstants;
import com.smu.portlogisticsdistributionsystem.util.UserHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.concurrent.TimeUnit;
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    StringRedisTemplate stringRedisTemplate;
    JwtUtil jwtUtil;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("authorization");
        if(StrUtil.isBlank(token)){
            response.setStatus(401);
            return false;
        }
        Map<Object,Object> entries = stringRedisTemplate.opsForHash().entries(RedisConstants.LOGIN_SESSION + token);

//        3.判断用户是否存在
        UserLoginDTO userLoginDTO = BeanUtil.fillBeanWithMap(entries,new UserLoginDTO(),false);
        if(userLoginDTO==null){
            response.setStatus(401);
            return false;
        }

        UserHolder.saveUser(userLoginDTO);
        stringRedisTemplate.expire(RedisConstants.LOGIN_SESSION + token,30, TimeUnit.MINUTES);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
