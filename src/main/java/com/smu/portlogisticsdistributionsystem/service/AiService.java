package com.smu.portlogisticsdistributionsystem.service;

import com.smu.portlogisticsdistributionsystem.dto.AiChatRequest;
import com.smu.portlogisticsdistributionsystem.dto.AiChatResponse;

/**
 * AI 智能体服务接口
 */
public interface AiService {

    /**
     * 通用聊天接口 - 支持上下文对话
     * @param request 聊天请求
     * @return 聊天响应
     */
    AiChatResponse chat(AiChatRequest request);

    /**
     * 清除会话记忆
     * @param sessionId 会话ID
     * @return 是否成功
     */
    boolean clearSession(String sessionId);
}
