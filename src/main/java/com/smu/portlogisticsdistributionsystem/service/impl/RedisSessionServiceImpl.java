package com.smu.portlogisticsdistributionsystem.service.impl;

import com.smu.portlogisticsdistributionsystem.service.RedisSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Redis 会话存储服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisSessionServiceImpl implements RedisSessionService {

    private final StringRedisTemplate redisTemplate;

    @Value("${ai.memory.expire-hours}")
    private int expireHours;

    private static final String SESSION_PREFIX = "ai:session:";

    @Override
    public void saveSession(String sessionId, String data) {
        String key = SESSION_PREFIX + sessionId;
        redisTemplate.opsForValue().set(key, data, expireHours, TimeUnit.HOURS);
        log.debug("Saved session: {}", sessionId);
    }

    @Override
    public String getSession(String sessionId) {
        String key = SESSION_PREFIX + sessionId;
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void deleteSession(String sessionId) {
        String key = SESSION_PREFIX + sessionId;
        redisTemplate.delete(key);
        log.debug("Deleted session: {}", sessionId);
    }

    @Override
    public boolean existsSession(String sessionId) {
        String key = SESSION_PREFIX + sessionId;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
