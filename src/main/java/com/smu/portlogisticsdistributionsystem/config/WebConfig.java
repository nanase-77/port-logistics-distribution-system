// WebConfig.java
package com.smu.portlogisticsdistributionsystem.config;

import com.smu.portlogisticsdistributionsystem.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")  // 拦截所有路径
                .excludePathPatterns(
                        // 排除不需要拦截的路径
                        "/api/auth/login",        // 登录接口
                        "/api/auth/register",     // 注册接口
                        "/api/user/code",         // 验证码接口
                        "/swagger-ui/**",         // Swagger UI
                        "/v3/api-docs/**",        // API 文档
                        "/swagger-resources/**",  // Swagger 资源
                        "/webjars/**",            // Webjars
                        "/doc.html",              // Knife4j
                        "/favicon.ico",           // 图标
                        "/error"                  // 错误页面
                )
                .order(1);  // 设置拦截器顺序
    }
}