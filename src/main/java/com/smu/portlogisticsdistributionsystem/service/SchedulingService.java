package com.smu.portlogisticsdistributionsystem.service;

import com.smu.portlogisticsdistributionsystem.dto.AlternativeRoute;
import com.smu.portlogisticsdistributionsystem.dto.OptimizationSuggestion;
import com.smu.portlogisticsdistributionsystem.dto.RouteCost;
import com.smu.portlogisticsdistributionsystem.dto.ScheduleResult;
import com.smu.portlogisticsdistributionsystem.dto.SchedulingRequest;

import java.util.List;
import java.util.Map;

/**
 * 智能调度引擎服务接口
 */
public interface SchedulingService {

    /**
     * 计算最短路径
     * @param fromPortId 起始港口ID
     * @param toPortId 目标港口ID
     * @return 路径规划结果
     */
    ScheduleResult calculateShortestPath(Long fromPortId, Long toPortId);

    /**
     * 货物分配调度
     * @param request 调度请求
     * @return 调度结果
     */
    ScheduleResult scheduleCargo(SchedulingRequest request);

    /**
     * 获取资源优化建议
     * @return 优化建议列表
     */
    List<OptimizationSuggestion> getOptimizationSuggestions();

    /**
     * 获取异常情况下的替代方案
     * @param originalRouteId 原路径ID
     * @param exceptionType 异常类型（如设备故障、天气异常等）
     * @return 替代路径列表
     */
    List<AlternativeRoute> getAlternativeRoutes(Long originalRouteId, String exceptionType);

    /**
     * 初始化港口拓扑图
     */
    void initPortGraph();

    /**
     * 获取图数据用于可视化
     * @return 图数据（节点和边）
     */
    Map<String, Object> getGraphData();

    /**
     * 同步港口数据到图数据库
     */
    void syncPortsToGraph();

    /**
     * 计算航线费用
     * @param fromPortId 起始港口ID
     * @param toPortId 目标港口ID
     * @param weight 货物重量（吨）
     * @return 费用详情
     */
    RouteCost calculateRouteCost(Long fromPortId, Long toPortId, double weight);

    /**
     * 查询最短路径（简化接口）
     * @param fromPortId 起始港口ID
     * @param toPortId 目标港口ID
     * @return 路径信息
     */
    Map<String, Object> findShortestPath(Long fromPortId, Long toPortId);
}
