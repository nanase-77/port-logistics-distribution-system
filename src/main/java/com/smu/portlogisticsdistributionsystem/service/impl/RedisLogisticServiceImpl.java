package com.smu.portlogisticsdistributionsystem.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.smu.portlogisticsdistributionsystem.entity.Logistic;
import com.smu.portlogisticsdistributionsystem.service.RedisLogisticService;
import com.smu.portlogisticsdistributionsystem.util.RedisConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisLogisticServiceImpl implements RedisLogisticService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Override
    public void addLogistic(Logistic logistic) {
        try {
            String logisticJson = objectMapper.writeValueAsString(logistic);
            redisTemplate.opsForHash().put(RedisConstants.PLDS_LOGISTICS, String.valueOf(logistic.getId()), logisticJson);
            log.info("Logistic {} added to Redis", logistic.getId());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize logistic", e);
        }
    }

    @Override
    public void updateLogistic(Logistic logistic) {
        addLogistic(logistic);
    }

    @Override
    public void deleteLogistic(Integer logisticId) {
        redisTemplate.opsForHash().delete(RedisConstants.PLDS_LOGISTICS, String.valueOf(logisticId));
        log.info("Logistic {} deleted from Redis", logisticId);
    }

    @Override
    public void clearAllLogistics() {
        redisTemplate.delete(RedisConstants.PLDS_LOGISTICS);
        log.info("All logistics cleared from Redis");
    }

    @Override
    public List<Logistic> getAllLogistics() {
        Map<Object, Object> logisticMap = redisTemplate.opsForHash().entries(RedisConstants.PLDS_LOGISTICS);
        List<Logistic> logistics = new ArrayList<>();
        for (Object value : logisticMap.values()) {
            try {
                logistics.add(objectMapper.readValue(value.toString(), Logistic.class));
            } catch (JsonProcessingException e) {
                log.error("Failed to deserialize logistic", e);
            }
        }
        return logistics;
    }

    @Override
    public Logistic getLogisticById(Integer logisticId) {
        Object logisticJson = redisTemplate.opsForHash().get(RedisConstants.PLDS_LOGISTICS, String.valueOf(logisticId));
        if (logisticJson != null) {
            try {
                return objectMapper.readValue(logisticJson.toString(), Logistic.class);
            } catch (JsonProcessingException e) {
                log.error("Failed to deserialize logistic", e);
            }
        }
        return null;
    }
}
