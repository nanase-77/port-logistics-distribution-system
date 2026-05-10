package com.smu.portlogisticsdistributionsystem.controller;

import com.smu.portlogisticsdistributionsystem.common.Result;
import com.smu.portlogisticsdistributionsystem.dto.AlternativeRoute;
import com.smu.portlogisticsdistributionsystem.dto.OptimizationSuggestion;
import com.smu.portlogisticsdistributionsystem.dto.RouteCost;
import com.smu.portlogisticsdistributionsystem.dto.ScheduleResult;
import com.smu.portlogisticsdistributionsystem.dto.SchedulingRequest;
import com.smu.portlogisticsdistributionsystem.service.SchedulingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 智能调度引擎控制器
 */
@RestController
@RequestMapping("/api/scheduling")
@RequiredArgsConstructor
public class SchedulingController {

    private final SchedulingService schedulingService;

    /**
     * 计算最短路径
     */
    @GetMapping("/path")
    public Result<ScheduleResult> calculateShortestPath(
            @RequestParam Long fromPortId,
            @RequestParam Long toPortId) {
        ScheduleResult result = schedulingService.calculateShortestPath(fromPortId, toPortId);
        return Result.success(result);
    }

    /**
     * 货物分配调度
     */
    @PostMapping("/cargo")
    public Result<ScheduleResult> scheduleCargo(@RequestBody SchedulingRequest request) {
        ScheduleResult result = schedulingService.scheduleCargo(request);
        return Result.success(result);
    }

    /**
     * 获取资源优化建议
     */
    @GetMapping("/suggestions")
    public Result<List<OptimizationSuggestion>> getOptimizationSuggestions() {
        List<OptimizationSuggestion> suggestions = schedulingService.getOptimizationSuggestions();
        return Result.success(suggestions);
    }

    /**
     * 获取异常情况下的替代方案
     */
    @GetMapping("/alternatives")
    public Result<List<AlternativeRoute>> getAlternativeRoutes(
            @RequestParam(required = false) Long originalRouteId,
            @RequestParam String exceptionType) {
        List<AlternativeRoute> routes = schedulingService.getAlternativeRoutes(originalRouteId, exceptionType);
        return Result.success(routes);
    }

    /**
     * 初始化港口拓扑图
     */
    @PostMapping("/init-graph")
    public Result<String> initPortGraph() {
        schedulingService.initPortGraph();
        return Result.success("港口拓扑图初始化成功");
    }

    /**
     * 获取图数据用于可视化
     */
    @GetMapping("/graph-data")
    public Result<Map<String, Object>> getGraphData() {
        Map<String, Object> graphData = schedulingService.getGraphData();
        return Result.success(graphData);
    }

    /**
     * 同步所有港口到图数据库
     */
    @PostMapping("/sync-ports")
    public Result<String> syncPorts() {
        schedulingService.syncPortsToGraph();
        return Result.success("港口数据同步成功");
    }

    /**
     * 计算航线费用
     */
    @GetMapping("/cost")
    public Result<RouteCost> calculateCost(
            @RequestParam Long fromPortId,
            @RequestParam Long toPortId,
            @RequestParam(defaultValue = "10") double weight) {
        RouteCost cost = schedulingService.calculateRouteCost(fromPortId, toPortId, weight);
        return Result.success(cost);
    }

    /**
     * 查询最短路径（简化接口）
     */
    @GetMapping("/shortest-path")
    public Result<Map<String, Object>> findShortestPath(
            @RequestParam Long fromPortId,
            @RequestParam Long toPortId) {
        Map<String, Object> result = schedulingService.findShortestPath(fromPortId, toPortId);
        return Result.success(result);
    }
}
