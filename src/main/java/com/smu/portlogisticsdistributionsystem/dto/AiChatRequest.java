package com.smu.portlogisticsdistributionsystem.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI 聊天请求 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiChatRequest {

    /**
     * 会话ID（可选，首次请求可空，系统会自动生成）
     */
    private String sessionId;

    /**
     * 用户查询内容
     */
    @NotBlank(message = "查询内容不能为空")
    private String message;

    /**
     * 查询类型：chat-通用聊天, query-智能查询, dispatch-调度建议, exception-异常处理
     */
    @Builder.Default
    private String type = "chat";
}
