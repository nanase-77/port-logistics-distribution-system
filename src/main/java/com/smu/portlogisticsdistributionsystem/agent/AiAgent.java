package com.smu.portlogisticsdistributionsystem.agent;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * AI智能体接口
 * 支持会话记忆，可以查询项目数据库中的真实数据
 */
public interface AiAgent {

    /**
     * 智能咨询服务
     * @param memoryId 会话记忆ID
     * @param userQuery 用户查询内容
     * @param context 检索到的上下文数据（JSON格式）
     * @return AI响应
     */
    @SystemMessage("""
            你是一个专业的港口物流AI助手，精通港口物流领域的各类业务。
            
            你的职责包括：
            1. 智能查询服务：提供港口资源、货物状态、运输工具等信息的自然语言查询
            2. 调度建议咨询：根据当前订单和资源状态，提供货物分配和路径规划建议
            3. 异常处理咨询：当系统检测到异常时，提供处理建议
            
            如果提供了上下文数据（context参数），请优先根据上下文数据回答用户问题。
            上下文数据是JSON格式，包含可能的港口、船舶、车辆、物流、集装箱、公司、订单等信息。
            如果上下文数据中有相关信息，请优先使用上下文数据进行回答。
            如果没有相关数据或数据为空，请说明情况。
            
            请使用友好、专业的语言回答用户问题。
            回答应清晰、有条理，必要时使用列表或表格形式展示信息。
            """)
    @UserMessage("""
            用户提问：{{userQuery}}
            
            上下文数据：{{context}}
            
            请根据上下文数据（如果有）和你的专业知识，为用户提供准确、专业的回答。
            如果上下文数据中有相关信息，请优先使用上下文数据进行回答。
            如果没有相关数据，请说明情况。
            """)
    String chat(@MemoryId String memoryId,
                @V("userQuery") String userQuery, 
                @V("context") String context);
}
