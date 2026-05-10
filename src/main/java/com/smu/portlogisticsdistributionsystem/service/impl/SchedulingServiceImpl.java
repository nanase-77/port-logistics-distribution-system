package com.smu.portlogisticsdistributionsystem.service.impl;

import com.smu.portlogisticsdistributionsystem.dto.AlternativeRoute;
import com.smu.portlogisticsdistributionsystem.dto.OptimizationSuggestion;
import com.smu.portlogisticsdistributionsystem.dto.RouteCost;
import com.smu.portlogisticsdistributionsystem.dto.ScheduleResult;
import com.smu.portlogisticsdistributionsystem.dto.SchedulingRequest;
import com.smu.portlogisticsdistributionsystem.entity.Car;
import com.smu.portlogisticsdistributionsystem.entity.Port;
import com.smu.portlogisticsdistributionsystem.entity.Ship;
import com.smu.portlogisticsdistributionsystem.mapper.CarMapper;
import com.smu.portlogisticsdistributionsystem.mapper.PortMapper;
import com.smu.portlogisticsdistributionsystem.mapper.ShipMapper;
import com.smu.portlogisticsdistributionsystem.service.GeoService;
import com.smu.portlogisticsdistributionsystem.service.SchedulingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.types.Node;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 智能调度引擎服务实现类
 * 使用Neo4j图数据库实现路径规划和资源调度
 * 集成高德地图 EagleMap API 进行距离计算
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SchedulingServiceImpl implements SchedulingService {

    private final Driver neo4jDriver;
    private final PortMapper portMapper;
    private final ShipMapper shipMapper;
    private final CarMapper carMapper;
    private final GeoService geoService;

    @Override
    public ScheduleResult calculateShortestPath(Long fromPortId, Long toPortId) {
        log.info("Calculating shortest path from port {} to port {}", fromPortId, toPortId);

        Port fromPort = portMapper.selectById(fromPortId);
        Port toPort = portMapper.selectById(toPortId);

        if (fromPort == null || toPort == null) {
            log.error("Port not found");
            return ScheduleResult.builder().status("FAILED").build();
        }

        List<ScheduleResult.PortNode> pathNodes = new ArrayList<>();
        double totalDistance = 0;

        try (Session session = neo4jDriver.session()) {
            String cypher = """
                MATCH (start:Port {id: $fromId}), (end:Port {id: $toId}),
                      path = shortestPath((start)-[*..10]->(end))
                RETURN nodes(path) as nodes
                """;

            Result result = session.run(cypher,
                    Map.of("fromId", fromPortId, "toId", toPortId));

            if (result.hasNext()) {
                Record record = result.next();
                List<Node> nodes = record.get("nodes").asList(val -> val.asNode());

                for (int i = 0; i < nodes.size(); i++) {
                    Node node = nodes.get(i);
                    ScheduleResult.PortNode portNode = ScheduleResult.PortNode.builder()
                            .portId(node.get("id").asLong())
                            .portName(node.get("name").asString())
                            .latitude(node.get("latitude").asDouble())
                            .longitude(node.get("longitude").asDouble())
                            .build();
                    pathNodes.add(portNode);

                    if (i > 0) {
                        ScheduleResult.PortNode prevNode = pathNodes.get(i - 1);
                        totalDistance += geoService.calculateDistance(
                                prevNode.getLatitude(), prevNode.getLongitude(),
                                portNode.getLatitude(), portNode.getLongitude());
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Neo4j query failed, using direct distance calculation", e);
        }

        if (pathNodes.isEmpty()) {
            totalDistance = geoService.calculateDistance(fromPort.getLatitude(), fromPort.getLongitude(),
                    toPort.getLatitude(), toPort.getLongitude());
            pathNodes.add(ScheduleResult.PortNode.builder()
                    .portId(fromPort.getId().longValue())
                    .portName(fromPort.getPortName())
                    .latitude(fromPort.getLatitude())
                    .longitude(fromPort.getLongitude())
                    .build());
            pathNodes.add(ScheduleResult.PortNode.builder()
                    .portId(toPort.getId().longValue())
                    .portName(toPort.getPortName())
                    .latitude(toPort.getLatitude())
                    .longitude(toPort.getLongitude())
                    .build());
        }

        double estimatedTime = geoService.calculateTime(totalDistance, 0);

        return ScheduleResult.builder()
                .routeId(System.currentTimeMillis())
                .routeName(fromPort.getPortName() + " -> " + toPort.getPortName())
                .totalDistance(Math.round(totalDistance * 100.0) / 100.0)
                .estimatedTime(Math.round(estimatedTime * 100.0) / 100.0)
                .ports(pathNodes)
                .status("SUCCESS")
                .build();
    }

    @Override
    public ScheduleResult scheduleCargo(SchedulingRequest request) {
        log.info("Scheduling cargo: {} from port {} to port {}",
                request.getCargoName(), request.getFromPortId(), request.getToPortId());

        ScheduleResult result = calculateShortestPath(request.getFromPortId(), request.getToPortId());

        List<Ship> availableShips = shipMapper.selectList(null);
        if (!availableShips.isEmpty()) {
            result.setShipName(availableShips.get(0).getShipName());
        }

        if (Boolean.TRUE.equals(request.getNeedCarTransport())) {
            Port toPort = portMapper.selectById(request.getToPortId());
            if (toPort != null) {
                List<Car> availableCars = carMapper.selectList(null).stream()
                        .filter(c -> "在用".equals(c.getStatus()) && 
                                c.getPortId() != null && c.getPortId().equals(toPort.getId()))
                        .collect(Collectors.toList());
                if (!availableCars.isEmpty()) {
                    result.setCarName(availableCars.get(0).getCarName());
                }
            }
        }

        result.setStatus("SCHEDULED");
        return result;
    }

    @Override
    public List<OptimizationSuggestion> getOptimizationSuggestions() {
        List<OptimizationSuggestion> suggestions = new ArrayList<>();

        List<Ship> ships = shipMapper.selectList(null);
        long activeShips = ships.size();
        if (activeShips > 0) {
            suggestions.add(OptimizationSuggestion.builder()
                    .suggestionId(System.currentTimeMillis())
                    .type("资源利用率")
                    .title("船舶资源状态")
                    .description("当前共有 " + ships.size() + " 艘船舶可用于调度")
                    .expectedBenefit(15.0)
                    .resourceName("船舶资源")
                    .build());
        }

        List<Car> cars = carMapper.selectList(null);
        long idleCars = cars.stream().filter(c -> "闲置".equals(c.getStatus())).count();
        if (idleCars > 0) {
            suggestions.add(OptimizationSuggestion.builder()
                    .suggestionId(System.currentTimeMillis() + 1)
                    .type("资源利用率")
                    .title("车辆资源优化")
                    .description("当前有 " + idleCars + " 辆车处于空闲状态")
                    .expectedBenefit(10.0)
                    .resourceName("车辆资源")
                    .build());
        }

        List<Port> ports = portMapper.selectList(null);
        suggestions.add(OptimizationSuggestion.builder()
                .suggestionId(System.currentTimeMillis() + 2)
                .type("负载均衡")
                .title("港口负载均衡建议")
                .description("当前系统共有 " + ports.size() + " 个港口，建议定期检查各港口运行状态")
                .expectedBenefit(20.0)
                .resourceName("港口资源")
                .build());

        return suggestions;
    }

    @Override
    public List<AlternativeRoute> getAlternativeRoutes(Long originalRouteId, String exceptionType) {
        List<AlternativeRoute> alternatives = new ArrayList<>();

        List<Port> ports = portMapper.selectList(null);
        if (ports.size() < 2) {
            return alternatives;
        }

        for (int i = 0; i < Math.min(3, ports.size() - 1); i++) {
            AlternativeRoute route = AlternativeRoute.builder()
                    .routeId(System.currentTimeMillis() + i)
                    .routeName("备选路线 " + (i + 1))
                    .totalDistance(Math.round((300 + Math.random() * 500) * 100.0) / 100.0)
                    .estimatedTime(Math.round((6 + Math.random() * 10) * 100.0) / 100.0)
                    .difference("避开" + exceptionType + "，选择替代航线")
                    .priority(5 - i)
                    .build();
            alternatives.add(route);
        }

        return alternatives;
    }

    @Override
    public void initPortGraph() {
        log.info("Initializing port graph in Neo4j");

        List<Port> ports = portMapper.selectList(null);

        try (Session session = neo4jDriver.session()) {
            session.run("MATCH (n:Port) DELETE n");
            session.run("MATCH ()-[r]->() DELETE r");

            for (Port port : ports) {
                session.run("""
                    CREATE (p:Port {
                        id: $id,
                        name: $name,
                        country: $country,
                        latitude: $latitude,
                        longitude: $longitude
                    })
                    """, Map.of(
                        "id", port.getId(),
                        "name", port.getPortName(),
                        "country", port.getCountry(),
                        "latitude", port.getLatitude(),
                        "longitude", port.getLongitude()
                    ));
            }

            for (int i = 0; i < ports.size(); i++) {
                for (int j = i + 1; j < ports.size(); j++) {
                    Port p1 = ports.get(i);
                    Port p2 = ports.get(j);
                    double distance = geoService.calculateDistance(p1.getLatitude(), p1.getLongitude(),
                            p2.getLatitude(), p2.getLongitude());

                    session.run("""
                        MATCH (a:Port {id: $fromId}), (b:Port {id: $toId})
                        CREATE (a)-[:ROUTE {distance: $distance}]->(b)
                        CREATE (b)-[:ROUTE {distance: $distance}]->(a)
                        """, Map.of(
                            "fromId", p1.getId(),
                            "toId", p2.getId(),
                            "distance", Math.round(distance * 100.0) / 100.0
                        ));
                }
            }

            log.info("Port graph initialized with {} ports", ports.size());
        } catch (Exception e) {
            log.warn("Failed to initialize Neo4j graph, continuing without it", e);
        }
    }

    @Override
    public Map<String, Object> getGraphData() {
        log.info("Fetching graph data for visualization");
        Map<String, Object> result = new HashMap<>();
        
        List<Map<String, Object>> nodes = new ArrayList<>();
        List<Map<String, Object>> edges = new ArrayList<>();

        List<Port> ports = portMapper.selectList(null);
        List<Ship> ships = shipMapper.selectList(null);

        int cols = 4;
        int padding = 80;
        int nodeWidth = 150;
        
        for (int i = 0; i < ports.size(); i++) {
            Port port = ports.get(i);
            Map<String, Object> node = new HashMap<>();
            node.put("id", port.getId());
            node.put("name", port.getPortName());
            node.put("type", "port");
            node.put("country", port.getCountry());
            node.put("latitude", port.getLatitude());
            node.put("longitude", port.getLongitude());
            node.put("x", padding + (i % cols) * nodeWidth);
            node.put("y", padding + (i / cols) * 120);
            nodes.add(node);
        }

        for (int i = 0; i < ports.size(); i++) {
            for (int j = i + 1; j < ports.size(); j++) {
                Port p1 = ports.get(i);
                Port p2 = ports.get(j);
                double distance = geoService.calculateDistance(p1.getLatitude(), p1.getLongitude(),
                        p2.getLatitude(), p2.getLongitude());
                
                Map<String, Object> edge = new HashMap<>();
                edge.put("from", p1.getId());
                edge.put("fromName", p1.getPortName());
                edge.put("to", p2.getId());
                edge.put("toName", p2.getPortName());
                edge.put("distance", Math.round(distance * 100.0) / 100.0);
                edge.put("label", Math.round(distance) + " km");
                edges.add(edge);
            }
        }

        result.put("nodes", nodes);
        result.put("edges", edges);
        result.put("stats", Map.of(
                "portCount", ports.size(),
                "shipCount", ships.size(),
                "carCount", 0,
                "routeCount", edges.size()
        ));

        return result;
    }

    @Override
    public void syncPortsToGraph() {
        log.info("Syncing all ports to Neo4j graph");

        try (Session session = neo4jDriver.session()) {
            session.run("MATCH (n:Port) DETACH DELETE n");

            List<Port> ports = portMapper.selectList(null);

            for (Port port : ports) {
                session.run("""
                    CREATE (p:Port {
                        id: $id,
                        name: $name,
                        country: $country,
                        latitude: $latitude,
                        longitude: $longitude
                    })
                    """, Map.of(
                        "id", port.getId(),
                        "name", port.getPortName(),
                        "country", port.getCountry(),
                        "latitude", port.getLatitude(),
                        "longitude", port.getLongitude()
                    ));
            }

            for (int i = 0; i < ports.size(); i++) {
                for (int j = i + 1; j < ports.size(); j++) {
                    Port p1 = ports.get(i);
                    Port p2 = ports.get(j);
                    double distance = geoService.calculateDistance(p1.getLatitude(), p1.getLongitude(),
                            p2.getLatitude(), p2.getLongitude());

                    session.run("""
                        MATCH (a:Port {id: $fromId}), (b:Port {id: $toId})
                        CREATE (a)-[:ROUTE {distance: $distance}]->(b)
                        CREATE (b)-[:ROUTE {distance: $distance}]->(a)
                        """, Map.of(
                            "fromId", p1.getId(),
                            "toId", p2.getId(),
                            "distance", Math.round(distance * 100.0) / 100.0
                        ));
                }
            }

            log.info("Successfully synced {} ports to graph", ports.size());
        } catch (Exception e) {
            log.error("Failed to sync ports to Neo4j: {}", e.getMessage());
            throw new RuntimeException("Failed to sync ports to Neo4j", e);
        }
    }

    @Override
    public RouteCost calculateRouteCost(Long fromPortId, Long toPortId, double weight) {
        Port fromPort = portMapper.selectById(fromPortId);
        Port toPort = portMapper.selectById(toPortId);

        if (fromPort == null || toPort == null) {
            return RouteCost.builder().build();
        }

        return geoService.calculateRoute(
                fromPort.getLatitude(), fromPort.getLongitude(),
                toPort.getLatitude(), toPort.getLongitude(),
                weight);
    }

    @Override
    public Map<String, Object> findShortestPath(Long fromPortId, Long toPortId) {
        Port fromPort = portMapper.selectById(fromPortId);
        Port toPort = portMapper.selectById(toPortId);

        Map<String, Object> result = new HashMap<>();

        if (fromPort == null || toPort == null) {
            result.put("status", "FAILED");
            result.put("message", "港口不存在");
            return result;
        }

        List<Map<String, Object>> pathNodes = new ArrayList<>();
        double totalDistance = 0;

        try (Session session = neo4jDriver.session()) {
            String cypher = """
                MATCH (start:Port {id: $fromId}), (end:Port {id: $toId}),
                      path = shortestPath((start)-[*..10]->(end))
                RETURN nodes(path) as nodes
                """;

            Result queryResult = session.run(cypher,
                    Map.of("fromId", fromPortId, "toId", toPortId));

            if (queryResult.hasNext()) {
                Record record = queryResult.next();
                List<Node> nodes = record.get("nodes").asList(val -> val.asNode());

                for (Node node : nodes) {
                    Map<String, Object> nodeInfo = new HashMap<>();
                    nodeInfo.put("id", node.get("id").asLong());
                    nodeInfo.put("name", node.get("name").asString());
                    nodeInfo.put("country", node.get("country").asString());
                    nodeInfo.put("latitude", node.get("latitude").asDouble());
                    nodeInfo.put("longitude", node.get("longitude").asDouble());
                    pathNodes.add(nodeInfo);
                }

                totalDistance = calculatePathDistance(pathNodes);
            }
        } catch (Exception e) {
            log.warn("Neo4j query failed, using direct path calculation", e);
        }

        if (pathNodes.isEmpty()) {
            totalDistance = geoService.calculateDistance(
                    fromPort.getLatitude(), fromPort.getLongitude(),
                    toPort.getLatitude(), toPort.getLongitude());

            Map<String, Object> fromNode = new HashMap<>();
            fromNode.put("id", fromPort.getId());
            fromNode.put("name", fromPort.getPortName());
            fromNode.put("country", fromPort.getCountry());
            fromNode.put("latitude", fromPort.getLatitude());
            fromNode.put("longitude", fromPort.getLongitude());
            pathNodes.add(fromNode);

            Map<String, Object> toNode = new HashMap<>();
            toNode.put("id", toPort.getId());
            toNode.put("name", toPort.getPortName());
            toNode.put("country", toPort.getCountry());
            toNode.put("latitude", toPort.getLatitude());
            toNode.put("longitude", toPort.getLongitude());
            pathNodes.add(toNode);
        }

        result.put("status", "SUCCESS");
        result.put("fromPort", fromPort.getPortName());
        result.put("toPort", toPort.getPortName());
        result.put("distance", totalDistance);
        result.put("estimatedTime", geoService.calculateTime(totalDistance, 0));
        result.put("nodes", pathNodes);
        result.put("pathLength", pathNodes.size());

        return result;
    }

    private double calculatePathDistance(List<Map<String, Object>> nodes) {
        double totalDistance = 0;
        for (int i = 1; i < nodes.size(); i++) {
            Map<String, Object> prev = nodes.get(i - 1);
            Map<String, Object> curr = nodes.get(i);
            totalDistance += geoService.calculateDistance(
                    (Double) prev.get("latitude"), (Double) prev.get("longitude"),
                    (Double) curr.get("latitude"), (Double) curr.get("longitude"));
        }
        return Math.round(totalDistance * 100.0) / 100.0;
    }
}
