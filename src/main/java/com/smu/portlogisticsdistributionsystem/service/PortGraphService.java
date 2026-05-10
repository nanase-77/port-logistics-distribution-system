package com.smu.portlogisticsdistributionsystem.service;

import com.smu.portlogisticsdistributionsystem.entity.Logistic;
import com.smu.portlogisticsdistributionsystem.entity.Port;
import com.smu.portlogisticsdistributionsystem.dto.RouteCost;

import java.util.List;
import java.util.Map;

/**
 * 港口图数据库服务接口
 */
public interface PortGraphService {

    /**
     * 添加港口到图数据库
     * @param port 港口实体
     */
    void addPortToGraph(Port port);

    /**
     * 从图数据库删除港口
     * @param portId 港口ID
     */
    void removePortFromGraph(Long portId);

    /**
     * 更新图数据库中的港口信息
     * @param port 港口实体
     */
    void updatePortInGraph(Port port);

    /**
     * 查询港口间的最短路径
     * @param fromPortId 起始港口ID
     * @param toPortId 目标港口ID
     * @return 路径信息
     */
    Map<String, Object> findShortestPath(Long fromPortId, Long toPortId);

    /**
     * 获取所有港口节点
     * @return 港口节点列表
     */
    List<Map<String, Object>> getAllPorts();

    /**
     * 获取港口间的航线信息
     * @return 航线列表
     */
    List<Map<String, Object>> getAllRoutes();

    /**
     * 计算港口间的费用
     * @param fromPortId 起始港口ID
     * @param toPortId 目标港口ID
     * @param weight 货物重量（吨）
     * @return 费用详情
     */
    RouteCost calculateRouteCost(Long fromPortId, Long toPortId, double weight);

    /**
     * 同步所有港口到图数据库
     */
    void syncAllPorts();

    /**
     * 获取图数据库统计信息
     * @return 统计信息
     */
    Map<String, Object> getGraphStats();

    /**
     * 添加物流信息到图数据库
     * @param logistic 物流实体
     */
    void addLogisticToGraph(Logistic logistic);

    /**
     * 从图数据库删除物流信息
     * @param logisticId 物流ID
     */
    void removeLogisticFromGraph(Long logisticId);

    /**
     * 更新图数据库中的物流信息
     * @param logistic 物流实体
     */
    void updateLogisticInGraph(Logistic logistic);

    /**
     * 获取物流路径信息
     * @param logisticId 物流ID
     * @return 物流路径信息
     */
    Map<String, Object> getLogisticPath(Long logisticId);
}
