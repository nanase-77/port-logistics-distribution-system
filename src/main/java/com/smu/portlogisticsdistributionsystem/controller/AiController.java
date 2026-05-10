package com.smu.portlogisticsdistributionsystem.controller;

import com.smu.portlogisticsdistributionsystem.dto.AiChatRequest;
import com.smu.portlogisticsdistributionsystem.dto.AiChatResponse;
import com.smu.portlogisticsdistributionsystem.service.AiService;
import com.smu.portlogisticsdistributionsystem.common.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * AI 智能体控制器
 * 提供智能咨询服务的 REST API
 */
@Slf4j
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    /**
     * 通用聊天接口 - 支持上下文对话
     * POST /api/ai/chat
     */
    @PostMapping("/chat")
    public Result<AiChatResponse> chat(@Valid @RequestBody AiChatRequest request) {
        log.info("AI chat request received: {}", request);
        AiChatResponse response = aiService.chat(request);
        return Result.success(response);
    }

    /**
     * 清除会话记忆
     * DELETE /api/ai/session/{sessionId}
     */
    @DeleteMapping("/session/{sessionId}")
    public Result<Boolean> clearSession(@PathVariable String sessionId) {
        log.info("Clear session request received: {}", sessionId);
        boolean success = aiService.clearSession(sessionId);
        return success ? Result.success(true) : Result.error("清除会话失败");
    }

    /**
     * 健康检查
     * GET /api/ai/health
     */
    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("AI Service is running");
    }
}
