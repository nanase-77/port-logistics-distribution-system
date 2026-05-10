package com.smu.portlogisticsdistributionsystem.controller;

import com.smu.portlogisticsdistributionsystem.common.Result;
import com.smu.portlogisticsdistributionsystem.dto.RouteCost;
import com.smu.portlogisticsdistributionsystem.entity.Port;
import com.smu.portlogisticsdistributionsystem.service.PortGraphService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 港口图数据库控制器
 * 提供港口图数据的管理和查询接口
 */
@RestController
@RequestMapping("/api/port-graph")
@RequiredArgsConstructor
public class PortGraphController {

    private final PortGraphService portGraphService;

    /**
     * 获取所有港口节点
     */
    @GetMapping("/ports")
    public Result<List<Map<String, Object>>> getAllPorts() {
        List<Map<String, Object>> ports = portGraphService.getAllPorts();
        return Result.success(ports);
    }

    /**
     * 获取所有航线信息
     */
    @GetMapping("/routes")
    public Result<List<Map<String, Object>>> getAllRoutes() {
        List<Map<String, Object>> routes = portGraphService.getAllRoutes();
        return Result.success(routes);
    }

    /**
     * 查询最短路径
     */
    @GetMapping("/path")
    public Result<Map<String, Object>> findShortestPath(
            @RequestParam Long fromPortId,
            @RequestParam Long toPortId) {
        Map<String, Object> path = portGraphService.findShortestPath(fromPortId, toPortId);
        return Result.success(path);
    }

    /**
     * 计算航线费用
     */
    @GetMapping("/cost")
    public Result<RouteCost> calculateRouteCost(
            @RequestParam Long fromPortId,
            @RequestParam Long toPortId,
            @RequestParam(defaultValue = "10") double weight) {
        RouteCost cost = portGraphService.calculateRouteCost(fromPortId, toPortId, weight);
        return Result.success(cost);
    }

    /**
     * 同步所有港口到图数据库
     */
    @PostMapping("/sync")
    public Result<String> syncAllPorts() {
        portGraphService.syncAllPorts();
        return Result.success("港口数据同步成功");
    }

    /**
     * 获取图数据库统计信息
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getGraphStats() {
        Map<String, Object> stats = portGraphService.getGraphStats();
        return Result.success(stats);
    }

    /**
     * 获取可视化数据
     */
    @GetMapping("/visualization")
    public Result<Map<String, Object>> getVisualizationData() {
        List<Map<String, Object>> nodes = portGraphService.getAllPorts();
        List<Map<String, Object>> edges = portGraphService.getAllRoutes();
        Map<String, Object> stats = portGraphService.getGraphStats();

        Map<String, Object> result = Map.of(
                "nodes", nodes,
                "edges", edges,
                "stats", stats
        );

        return Result.success(result);
    }
}
