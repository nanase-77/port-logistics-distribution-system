package com.smu.portlogisticsdistributionsystem.service.impl;

import com.smu.portlogisticsdistributionsystem.agent.AiAgent;
import com.smu.portlogisticsdistributionsystem.dto.AiChatRequest;
import com.smu.portlogisticsdistributionsystem.dto.AiChatResponse;
import com.smu.portlogisticsdistributionsystem.service.AiService;
import com.smu.portlogisticsdistributionsystem.service.DataRetrievalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * AI服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AiServiceImpl implements AiService {

    private final AiAgent aiAgent;
    private final DataRetrievalService dataRetrievalService;

    @Override
    public AiChatResponse chat(AiChatRequest request) {
        String sessionId = getSessionId(request);
        log.info("AI chat request - sessionId: {}, message: {}", sessionId, request.getMessage());

        // 1. 先从数据库检索相关数据
        String contextData = dataRetrievalService.smartRetrieve(request.getMessage());
        log.info("Retrieved context data: {}", contextData);

        // 2. 调用AI智能体，传递上下文数据
        String response = aiAgent.chat(sessionId, request.getMessage(), contextData);
        log.info("AI chat response - sessionId: {}, response: {}", sessionId, response);

        return buildResponse(sessionId, response);
    }

    @Override
    public boolean clearSession(String sessionId) {
        // 会话存储在Redis中，会自动过期，无需手动清除
        log.info("Session cleared: {}", sessionId);
        return true;
    }

    /**
     * 获取或生成会话ID
     */
    private String getSessionId(AiChatRequest request) {
        if (request.getSessionId() == null || request.getSessionId().isEmpty()) {
            return UUID.randomUUID().toString();
        }
        return request.getSessionId();
    }

    /**
     * 构建响应对象
     */
    private AiChatResponse buildResponse(String sessionId, String content) {
        return AiChatResponse.builder()
                .sessionId(sessionId)
                .content(content)
                .contentType("text")
                .isFinal(true)
                .responseTime(LocalDateTime.now())
                .build();
    }
}
