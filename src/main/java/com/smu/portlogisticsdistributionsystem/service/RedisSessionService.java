package com.smu.portlogisticsdistributionsystem.service;

/**
 * Redis 会话存储服务接口
 */
public interface RedisSessionService {

    /**
     * 保存会话数据
     * @param sessionId 会话ID
     * @param data 会话数据
     */
    void saveSession(String sessionId, String data);

    /**
     * 获取会话数据
     * @param sessionId 会话ID
     * @return 会话数据
     */
    String getSession(String sessionId);

    /**
     * 删除会话
     * @param sessionId 会话ID
     */
    void deleteSession(String sessionId);

    /**
     * 检查会话是否存在
     * @param sessionId 会话ID
     * @return 是否存在
     */
    boolean existsSession(String sessionId);
}
