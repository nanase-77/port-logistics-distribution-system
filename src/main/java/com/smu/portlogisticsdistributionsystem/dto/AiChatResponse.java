package com.smu.portlogisticsdistributionsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * AI 聊天响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiChatResponse {

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 响应内容
     */
    private String content;

    /**
     * 响应类型：text-纯文本, table-表格, list-列表
     */
    @Builder.Default
    private String contentType = "text";

    /**
     * 是否是最后一条消息
     */
    @Builder.Default
    private Boolean isFinal = true;

    /**
     * 响应时间
     */
    @Builder.Default
    private LocalDateTime responseTime = LocalDateTime.now();
}
