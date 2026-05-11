package com.smu.portlogisticsdistributionsystem.config;

import com.smu.portlogisticsdistributionsystem.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        // 登录注册接口
                        "/api/auth/**",
                        // 静态资源
                        "/static/**",
                        "/favicon.ico",
                        // Swagger/Knife4j 文档
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**",
                        "/api-docs/**",
                        "/doc.html",
                        // 测试接口
                        "/test/**",
                        // 健康检查
                        "/health"
                );
    }
}
