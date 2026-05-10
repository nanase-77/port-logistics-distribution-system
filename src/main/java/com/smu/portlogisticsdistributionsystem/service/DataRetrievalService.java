package com.smu.portlogisticsdistributionsystem.service;

/**
 * 数据检索服务接口
 * 用于从数据库中检索港口物流相关数据
 */
public interface DataRetrievalService {

    /**
     * 根据查询关键词检索港口数据
     * @param query 查询关键词
     * @return 港口数据JSON字符串
     */
    String retrievePorts(String query);

    /**
     * 根据查询关键词检索船舶数据
     * @param query 查询关键词
     * @return 船舶数据JSON字符串
     */
    String retrieveShips(String query);

    /**
     * 根据查询关键词检索车辆数据
     * @param query 查询关键词
     * @return 车辆数据JSON字符串
     */
    String retrieveCars(String query);

    /**
     * 根据查询关键词检索物流数据
     * @param query 查询关键词
     * @return 物流数据JSON字符串
     */
    String retrieveLogistics(String query);

    /**
     * 根据查询关键词检索集装箱数据
     * @param query 查询关键词
     * @return 集装箱数据JSON字符串
     */
    String retrieveContainers(String query);

    /**
     * 根据查询关键词检索公司数据
     * @param query 查询关键词
     * @return 公司数据JSON字符串
     */
    String retrieveCompanies(String query);

    /**
     * 根据查询关键词检索订单数据
     * @param query 查询关键词
     * @return 订单数据JSON字符串
     */
    String retrieveOrders(String query);

    /**
     * 智能检索 - 根据查询自动判断需要检索的数据类型
     * @param query 用户查询
     * @return 检索到的数据JSON字符串
     */
    String smartRetrieve(String query);
}
