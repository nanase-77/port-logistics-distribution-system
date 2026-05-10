package com.smu.portlogisticsdistributionsystem.config;

import com.smu.portlogisticsdistributionsystem.agent.AiAgent;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * AI配置类
 */
@Configuration
public class AiConfig {

    @Value("${ai.openai.api-key}")
    private String apiKey;

    @Value("${ai.openai.model}")
    private String model;

    @Value("${ai.openai.base-url:}")
    private String baseUrl;

    /**
     * 创建 OpenAI Chat 模型
     */
    @Bean
    public ChatLanguageModel chatLanguageModel() {
        OpenAiChatModel.OpenAiChatModelBuilder builder = OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName(model)
                .temperature(0.7)
                .timeout(Duration.ofMinutes(5));

        if (baseUrl != null && !baseUrl.isEmpty()) {
            builder.baseUrl(baseUrl);
        }

        return builder.build();
    }

    /**
     * 创建AI智能体服务
     */
    @Bean
    public AiAgent aiAgent(ChatLanguageModel chatLanguageModel) {
        return AiServices.builder(AiAgent.class)
                .chatLanguageModel(chatLanguageModel)
                .build();
    }
}
