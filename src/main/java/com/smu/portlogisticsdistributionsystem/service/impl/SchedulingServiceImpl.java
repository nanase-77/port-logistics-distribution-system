package com.smu.portlogisticsdistributionsystem.service.impl;


import com.smu.portlogisticsdistributionsystem.dto.*;
import com.smu.portlogisticsdistributionsystem.entity.Car;
import com.smu.portlogisticsdistributionsystem.entity.Container;
import com.smu.portlogisticsdistributionsystem.entity.Logistic;
import com.smu.portlogisticsdistributionsystem.entity.Order;
import com.smu.portlogisticsdistributionsystem.entity.Port;
import com.smu.portlogisticsdistributionsystem.entity.Ship;
import com.smu.portlogisticsdistributionsystem.mapper.CarMapper;
import com.smu.portlogisticsdistributionsystem.mapper.ContainerMapper;
import com.smu.portlogisticsdistributionsystem.mapper.LogisticMapper;
import com.smu.portlogisticsdistributionsystem.mapper.OrderMapper;
import com.smu.portlogisticsdistributionsystem.mapper.PortMapper;
import com.smu.portlogisticsdistributionsystem.mapper.ShipMapper;
import com.smu.portlogisticsdistributionsystem.service.GeoService;
import com.smu.portlogisticsdistributionsystem.service.RedisLogisticService;
import com.smu.portlogisticsdistributionsystem.service.RedisOrderService;
import com.smu.portlogisticsdistributionsystem.service.SchedulingService;
import com.smu.portlogisticsdistributionsystem.util.UserHolder;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.types.Node;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    private final ContainerMapper containerMapper;
    private final CarMapper carMapper;
    private final OrderMapper orderMapper;
    private final LogisticMapper logisticMapper;
    private final GeoService geoService;
    private final RedisOrderService redisOrderService;
    private final RedisLogisticService redisLogisticService;

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
    @Transactional
    public ScheduleResult scheduleCargo(SchedulingRequest request) {
        log.info("Scheduling cargo: {} from port {} to port {}",
                request.getCargoName(), request.getFromPortId(), request.getToPortId());

        ScheduleResult result = calculateShortestPath(request.getFromPortId(), request.getToPortId());

        Integer requiredTeu = request.getTeu();
        if (requiredTeu == null || requiredTeu <= 0) {
            requiredTeu = 1;
        }

        // 分配船舶 (status: 0=空闲, 1=在用) - 优先选择在起始港口的船舶，且容量足够
        Ship allocatedShip = allocateShip(request.getFromPortId(), requiredTeu);
        if (allocatedShip != null) {
            result.setShipId(allocatedShip.getId());
            result.setShipName(allocatedShip.getShipName());
            result.setShipCapacity(allocatedShip.getCapacity());
            result.setShipCurrentTeu(allocatedShip.getCurrentTeu());
        }

        // 分配集装箱 (status: 0=空闲, 1=在用) - 根据容量智能分配
        Container allocatedContainer = allocateContainer(request.getCargoName(), requiredTeu);
        if (allocatedContainer != null) {
            result.setContainerId(allocatedContainer.getId());
            result.setContainerCode(String.valueOf(allocatedContainer.getId()));
            result.setContainerCapacity(allocatedContainer.getCapacity());
            result.setContainerContent(allocatedContainer.getContent());
        }

        // 分配车辆（集卡运输）(status: '闲置'/'在用') - 从起始港口分配
        if (Boolean.TRUE.equals(request.getNeedCarTransport())) {
            Car allocatedCar = allocateCar(request.getFromPortId());
            if (allocatedCar != null) {
                result.setCarId(allocatedCar.getId());
                Port fromPort = portMapper.selectById(request.getFromPortId());
                result.setCarName(allocatedCar.getCarName());
                result.setCarPort(fromPort != null ? fromPort.getPortName() : "-");
            }
        }

        // 保存货物信息
        result.setCargoName(request.getCargoName());
        result.setVolume(request.getVolume());
        result.setTeu(requiredTeu);
        result.setFromPortId(request.getFromPortId());
        result.setToPortId(request.getToPortId());

        result.setStatus("SCHEDULED");
        return result;
    }

    /**
     * 确认订单，更新数据库
     */
    @Override
    @Transactional
    public void confirmOrder(ScheduleResult result) {
        // 1. 获取当前用户
        Integer userId = null;
        UserLoginDTO currentUser = UserHolder.getUser();
        if (currentUser != null) {
            userId = currentUser.getId();
        }

        // 2. 创建订单记录
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String orderNumber = "ORD" + sdf.format(new Date());
        Order order = new Order();
        order.setOrderNumber(orderNumber);
        order.setUserId(userId);
        order.setStatus("进行中");
        if (result.getContainerId() != null) {
            order.setContainerIds(String.valueOf(result.getContainerId()));
        }
        orderMapper.insert(order);

        // 2. 创建物流跟踪记录
        Logistic logistic = new Logistic();
        logistic.setOrderId(order.getId());
        logistic.setStartPortId(result.getFromPortId() != null ? result.getFromPortId().intValue() : null);
        logistic.setEndPortId(result.getToPortId() != null ? result.getToPortId().intValue() : null);
        logistic.setCurrentPortId(result.getFromPortId() != null ? result.getFromPortId().intValue() : null);
        logistic.setShipId(result.getShipId());
        logistic.setCarId(result.getCarId());
        logisticMapper.insert(logistic);

        // 3. 更新船舶
        if (result.getShipId() != null) {
            Ship ship = shipMapper.selectById(result.getShipId());
            if (ship != null) {
                ship.setCurrentTeu(ship.getCurrentTeu() + result.getTeu());
                shipMapper.updateById(ship);
            }
        }

        // 4. 更新集装箱
        if (result.getContainerId() != null) {
            Container container = containerMapper.selectById(result.getContainerId());
            if (container != null) {
                container.setContent(result.getCargoName());
                container.setStatus(1);
                containerMapper.updateById(container);
            }
        }

        // 5. 更新车辆
        if (result.getCarId() != null) {
            Car car = carMapper.selectById(result.getCarId());
            if (car != null) {
                car.setStatus("在用");
                carMapper.updateById(car);
            }
        }

        // 6. 清除 Redis 缓存，确保下次查询从数据库获取最新数据
        try {
            redisOrderService.clearAllOrders();
            redisLogisticService.clearAllLogistics();
            log.info("Cleared order and logistics cache after scheduling");
        } catch (Exception e) {
            log.warn("Failed to clear Redis cache: {}", e.getMessage());
        }
    }

    /**
     * 分配船舶 - 优先选择在起始港口且容量足够的船舶
     */
    private Ship allocateShip(Long fromPortId, Integer requiredTeu) {
        List<Ship> availableShips = shipMapper.selectList(new LambdaQueryWrapper<Ship>()
                .eq(Ship::getStatus, 0)
                .eq(Ship::getCurrentPortId, fromPortId.intValue()))
                .stream()
                .filter(s -> {
                    int currentTeu = s.getCurrentTeu() != null ? s.getCurrentTeu() : 0;
                    int capacity = s.getCapacity() != null ? s.getCapacity() : 0;
                    return capacity >= currentTeu + requiredTeu;
                })
                .collect(Collectors.toList());

        if (!availableShips.isEmpty()) {
            // 选择剩余容量最大的船舶
            return availableShips.stream()
                    .max((s1, s2) -> {
                        int remaining1 = (s1.getCapacity() != null ? s1.getCapacity() : 0) - 
                                        (s1.getCurrentTeu() != null ? s1.getCurrentTeu() : 0);
                        int remaining2 = (s2.getCapacity() != null ? s2.getCapacity() : 0) - 
                                        (s2.getCurrentTeu() != null ? s2.getCurrentTeu() : 0);
                        return Integer.compare(remaining1, remaining2);
                    })
                    .orElse(availableShips.get(0));
        }

        log.warn("No available ship at port {} with capacity >= {}", fromPortId, requiredTeu);
        return null;
    }

    /**
     * 分配集装箱 - 根据容量智能分配
     */
    private Container allocateContainer(String cargoName, Integer requiredTeu) {
        List<Container> availableContainers = containerMapper.selectList(new LambdaQueryWrapper<Container>()
                .eq(Container::getStatus, 0))
                .stream()
                .filter(c -> c.getCapacity() != null && c.getCapacity() >= requiredTeu)
                .collect(Collectors.toList());

        if (!availableContainers.isEmpty()) {
            // 选择容量最接近需求的集装箱
            Container container = availableContainers.stream()
                    .min((c1, c2) -> {
                        int diff1 = Math.abs(c1.getCapacity() - requiredTeu);
                        int diff2 = Math.abs(c2.getCapacity() - requiredTeu);
                        return Integer.compare(diff1, diff2);
                    })
                    .orElse(availableContainers.get(0));

            log.info("Found container {} with capacity: {}", container.getId(), container.getCapacity());
            return container;
        }

        log.warn("No available container with capacity >= {}", requiredTeu);
        return null;
    }

    /**
     * 分配车辆 - 从指定港口分配闲置车辆
     */
    private Car allocateCar(Long portId) {
        List<Car> availableCars = carMapper.selectList(new LambdaQueryWrapper<Car>()
                .eq(Car::getStatus, "闲置")
                .eq(Car::getPortId, portId.intValue()));

        if (!availableCars.isEmpty()) {
            return availableCars.get(0);
        }

        log.warn("No available car at port {}", portId);
        return null;
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
