package com.smu.portlogisticsdistributionsystem.service.impl;

import com.smu.portlogisticsdistributionsystem.dto.RouteCost;
import com.smu.portlogisticsdistributionsystem.entity.Logistic;
import com.smu.portlogisticsdistributionsystem.entity.Port;
import com.smu.portlogisticsdistributionsystem.mapper.PortMapper;
import com.smu.portlogisticsdistributionsystem.service.GeoService;
import com.smu.portlogisticsdistributionsystem.service.PortGraphService;
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

/**
 * 港口图数据库服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PortGraphServiceImpl implements PortGraphService {

    private final Driver neo4jDriver;
    private final PortMapper portMapper;
    private final GeoService geoService;

    @Override
    public void addPortToGraph(Port port) {
        log.info("Adding port to graph: {}", port.getPortName());
        
        try (Session session = neo4jDriver.session()) {
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
            
            updateRoutesForNewPort(port);
            log.info("Port {} added to graph successfully", port.getPortName());
        } catch (Exception e) {
            log.warn("Failed to add port to Neo4j: {}", e.getMessage());
        }
    }

    private void updateRoutesForNewPort(Port newPort) {
        try (Session session = neo4jDriver.session()) {
            Result result = session.run("MATCH (p:Port) RETURN p");
            
            while (result.hasNext()) {
                Record record = result.next();
                Node node = record.get("p").asNode();
                Long existingId = node.get("id").asLong();
                
                if (!existingId.equals(newPort.getId())) {
                    double distance = geoService.calculateDistance(
                            newPort.getLatitude(), newPort.getLongitude(),
                            node.get("latitude").asDouble(), node.get("longitude").asDouble());
                    
                    session.run("""
                        MATCH (a:Port {id: $fromId}), (b:Port {id: $toId})
                        CREATE (a)-[:ROUTE {distance: $distance}]->(b)
                        CREATE (b)-[:ROUTE {distance: $distance}]->(a)
                        """, Map.of(
                            "fromId", newPort.getId(),
                            "toId", existingId,
                            "distance", Math.round(distance * 100.0) / 100.0
                        ));
                }
            }
        } catch (Exception e) {
            log.warn("Failed to update routes: {}", e.getMessage());
        }
    }

    @Override
    public void removePortFromGraph(Long portId) {
        log.info("Removing port from graph: {}", portId);
        
        try (Session session = neo4jDriver.session()) {
            session.run("MATCH (p:Port {id: $id}) DETACH DELETE p", Map.of("id", portId));
            log.info("Port {} removed from graph", portId);
        } catch (Exception e) {
            log.warn("Failed to remove port from Neo4j: {}", e.getMessage());
        }
    }

    @Override
    public void updatePortInGraph(Port port) {
        log.info("Updating port in graph: {}", port.getPortName());
        
        try (Session session = neo4jDriver.session()) {
            session.run("""
                MATCH (p:Port {id: $id})
                SET p.name = $name, p.country = $country, 
                    p.latitude = $latitude, p.longitude = $longitude
                """, Map.of(
                    "id", port.getId(),
                    "name", port.getPortName(),
                    "country", port.getCountry(),
                    "latitude", port.getLatitude(),
                    "longitude", port.getLongitude()
                ));
            
            session.run("""
                MATCH ()-[r:ROUTE]->(p:Port {id: $id})-[r2:ROUTE]->()
                DELETE r, r2
                """, Map.of("id", port.getId()));
            
            updateRoutesForNewPort(port);
            log.info("Port {} updated in graph", port.getPortName());
        } catch (Exception e) {
            log.warn("Failed to update port in Neo4j: {}", e.getMessage());
        }
    }

    @Override
    public Map<String, Object> findShortestPath(Long fromPortId, Long toPortId) {
        log.info("Finding shortest path from port {} to port {}", fromPortId, toPortId);
        Map<String, Object> result = new HashMap<>();
        
        Port fromPort = portMapper.selectById(fromPortId);
        Port toPort = portMapper.selectById(toPortId);
        
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
                RETURN nodes(path) as nodes, relationships(path) as rels
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

    @Override
    public List<Map<String, Object>> getAllPorts() {
        List<Map<String, Object>> ports = new ArrayList<>();
        
        try (Session session = neo4jDriver.session()) {
            Result result = session.run("MATCH (p:Port) RETURN p ORDER BY p.id");
            
            while (result.hasNext()) {
                Record record = result.next();
                Node node = record.get("p").asNode();
                
                Map<String, Object> portInfo = new HashMap<>();
                portInfo.put("id", node.get("id").asLong());
                portInfo.put("name", node.get("name").asString());
                portInfo.put("country", node.get("country").asString());
                portInfo.put("latitude", node.get("latitude").asDouble());
                portInfo.put("longitude", node.get("longitude").asDouble());
                ports.add(portInfo);
            }
        } catch (Exception e) {
            log.warn("Failed to get ports from Neo4j, falling back to MySQL", e);
            
            List<Port> dbPorts = portMapper.selectList(null);
            for (Port port : dbPorts) {
                Map<String, Object> portInfo = new HashMap<>();
                portInfo.put("id", port.getId());
                portInfo.put("name", port.getPortName());
                portInfo.put("country", port.getCountry());
                portInfo.put("latitude", port.getLatitude());
                portInfo.put("longitude", port.getLongitude());
                ports.add(portInfo);
            }
        }
        
        return ports;
    }

    @Override
    public List<Map<String, Object>> getAllRoutes() {
        List<Map<String, Object>> routes = new ArrayList<>();
        
        try (Session session = neo4jDriver.session()) {
            Result result = session.run("""
                MATCH (a:Port)-[r:ROUTE]->(b:Port)
                WHERE a.id < b.id
                RETURN a, b, r.distance as distance
                """);
            
            while (result.hasNext()) {
                Record record = result.next();
                Node a = record.get("a").asNode();
                Node b = record.get("b").asNode();
                
                Map<String, Object> route = new HashMap<>();
                route.put("fromId", a.get("id").asLong());
                route.put("fromName", a.get("name").asString());
                route.put("toId", b.get("id").asLong());
                route.put("toName", b.get("name").asString());
                route.put("distance", record.get("distance").asDouble());
                routes.add(route);
            }
        } catch (Exception e) {
            log.warn("Failed to get routes from Neo4j, generating from ports", e);
            
            List<Port> ports = portMapper.selectList(null);
            for (int i = 0; i < ports.size(); i++) {
                for (int j = i + 1; j < ports.size(); j++) {
                    Port p1 = ports.get(i);
                    Port p2 = ports.get(j);
                    
                    Map<String, Object> route = new HashMap<>();
                    route.put("fromId", p1.getId());
                    route.put("fromName", p1.getPortName());
                    route.put("toId", p2.getId());
                    route.put("toName", p2.getPortName());
                    route.put("distance", geoService.calculateDistance(
                            p1.getLatitude(), p1.getLongitude(),
                            p2.getLatitude(), p2.getLongitude()));
                    routes.add(route);
                }
            }
        }
        
        return routes;
    }

    @Override
    public RouteCost calculateRouteCost(Long fromPortId, Long toPortId, double weight) {
        Port fromPort = portMapper.selectById(fromPortId);
        Port toPort = portMapper.selectById(toPortId);
        
        if (fromPort == null || toPort == null) {
            return RouteCost.builder().build();
        }
        
        double distance = geoService.calculateDistance(
                fromPort.getLatitude(), fromPort.getLongitude(),
                toPort.getLatitude(), toPort.getLongitude());
        
        RouteCost cost = geoService.calculateRoute(
                fromPort.getLatitude(), fromPort.getLongitude(),
                toPort.getLatitude(), toPort.getLongitude(),
                weight);
        
        return cost;
    }

    @Override
    public void syncAllPorts() {
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
                    double distance = geoService.calculateDistance(
                            p1.getLatitude(), p1.getLongitude(),
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
        }
    }

    @Override
    public Map<String, Object> getGraphStats() {
        Map<String, Object> stats = new HashMap<>();
        
        try (Session session = neo4jDriver.session()) {
            Result portCount = session.run("MATCH (p:Port) RETURN count(p) as count");
            stats.put("portCount", portCount.hasNext() ? portCount.next().get("count").asLong() : 0);
            
            Result routeCount = session.run("MATCH ()-[r:ROUTE]->() RETURN count(r) as count");
            stats.put("routeCount", routeCount.hasNext() ? routeCount.next().get("count").asLong() / 2 : 0);
            
            Result logisticCount = session.run("MATCH (l:Logistic) RETURN count(l) as count");
            stats.put("logisticCount", logisticCount.hasNext() ? logisticCount.next().get("count").asLong() : 0);
        } catch (Exception e) {
            log.warn("Failed to get stats from Neo4j", e);
            List<Port> ports = portMapper.selectList(null);
            stats.put("portCount", ports.size());
            stats.put("routeCount", ports.size() * (ports.size() - 1) / 2);
            stats.put("logisticCount", 0);
        }
        
        return stats;
    }

    @Override
    public void addLogisticToGraph(Logistic logistic) {
        log.info("Adding logistic to graph: {}", logistic.getId());
        
        try (Session session = neo4jDriver.session()) {
            session.run("""
                CREATE (l:Logistic {
                    id: $id,
                    orderId: $orderId,
                    startPortId: $startPortId,
                    endPortId: $endPortId,
                    currentPortId: $currentPortId,
                    shipId: $shipId,
                    carId: $carId
                })
                """, Map.of(
                    "id", logistic.getId(),
                    "orderId", logistic.getOrderId(),
                    "startPortId", logistic.getStartPortId(),
                    "endPortId", logistic.getEndPortId(),
                    "currentPortId", logistic.getCurrentPortId(),
                    "shipId", logistic.getShipId(),
                    "carId", logistic.getCarId()
                ));
            
            if (logistic.getStartPortId() != null) {
                session.run("""
                    MATCH (l:Logistic {id: $logisticId}), (p:Port {id: $portId})
                    CREATE (l)-[:DEPARTS_FROM]->(p)
                    """, Map.of("logisticId", logistic.getId(), "portId", logistic.getStartPortId()));
            }
            
            if (logistic.getEndPortId() != null) {
                session.run("""
                    MATCH (l:Logistic {id: $logisticId}), (p:Port {id: $portId})
                    CREATE (l)-[:ARRIVES_AT]->(p)
                    """, Map.of("logisticId", logistic.getId(), "portId", logistic.getEndPortId()));
            }
            
            if (logistic.getCurrentPortId() != null) {
                session.run("""
                    MATCH (l:Logistic {id: $logisticId}), (p:Port {id: $portId})
                    CREATE (l)-[:CURRENTLY_AT]->(p)
                    """, Map.of("logisticId", logistic.getId(), "portId", logistic.getCurrentPortId()));
            }
            
            log.info("Logistic {} added to graph successfully", logistic.getId());
        } catch (Exception e) {
            log.warn("Failed to add logistic to Neo4j: {}", e.getMessage());
        }
    }

    @Override
    public void removeLogisticFromGraph(Long logisticId) {
        log.info("Removing logistic from graph: {}", logisticId);
        
        try (Session session = neo4jDriver.session()) {
            session.run("MATCH (l:Logistic {id: $id}) DETACH DELETE l", Map.of("id", logisticId));
            log.info("Logistic {} removed from graph", logisticId);
        } catch (Exception e) {
            log.warn("Failed to remove logistic from Neo4j: {}", e.getMessage());
        }
    }

    @Override
    public void updateLogisticInGraph(Logistic logistic) {
        log.info("Updating logistic in graph: {}", logistic.getId());
        
        try (Session session = neo4jDriver.session()) {
            session.run("""
                MATCH (l:Logistic {id: $id})
                SET l.orderId = $orderId, l.startPortId = $startPortId, 
                    l.endPortId = $endPortId, l.currentPortId = $currentPortId,
                    l.shipId = $shipId, l.carId = $carId
                """, Map.of(
                    "id", logistic.getId(),
                    "orderId", logistic.getOrderId(),
                    "startPortId", logistic.getStartPortId(),
                    "endPortId", logistic.getEndPortId(),
                    "currentPortId", logistic.getCurrentPortId(),
                    "shipId", logistic.getShipId(),
                    "carId", logistic.getCarId()
                ));
            
            session.run("""
                MATCH (l:Logistic {id: $id})-[r]-() DELETE r
                """, Map.of("id", logistic.getId()));
            
            if (logistic.getStartPortId() != null) {
                session.run("""
                    MATCH (l:Logistic {id: $logisticId}), (p:Port {id: $portId})
                    CREATE (l)-[:DEPARTS_FROM]->(p)
                    """, Map.of("logisticId", logistic.getId(), "portId", logistic.getStartPortId()));
            }
            
            if (logistic.getEndPortId() != null) {
                session.run("""
                    MATCH (l:Logistic {id: $logisticId}), (p:Port {id: $portId})
                    CREATE (l)-[:ARRIVES_AT]->(p)
                    """, Map.of("logisticId", logistic.getId(), "portId", logistic.getEndPortId()));
            }
            
            if (logistic.getCurrentPortId() != null) {
                session.run("""
                    MATCH (l:Logistic {id: $logisticId}), (p:Port {id: $portId})
                    CREATE (l)-[:CURRENTLY_AT]->(p)
                    """, Map.of("logisticId", logistic.getId(), "portId", logistic.getCurrentPortId()));
            }
            
            log.info("Logistic {} updated in graph", logistic.getId());
        } catch (Exception e) {
            log.warn("Failed to update logistic in Neo4j: {}", e.getMessage());
        }
    }

    @Override
    public Map<String, Object> getLogisticPath(Long logisticId) {
        log.info("Getting logistic path: {}", logisticId);
        Map<String, Object> result = new HashMap<>();
        
        try (Session session = neo4jDriver.session()) {
            Result logisticResult = session.run("""
                MATCH (l:Logistic {id: $id})
                OPTIONAL MATCH (l)-[:DEPARTS_FROM]->(start:Port)
                OPTIONAL MATCH (l)-[:ARRIVES_AT]->(end:Port)
                OPTIONAL MATCH (l)-[:CURRENTLY_AT]->(current:Port)
                RETURN l, start, end, current
                """, Map.of("id", logisticId));
            
            if (logisticResult.hasNext()) {
                Record record = logisticResult.next();
                
                Map<String, Object> logisticInfo = new HashMap<>();
                if (record.get("l").isNotNull()) {
                    Node l = record.get("l").asNode();
                    logisticInfo.put("id", l.get("id").asLong());
                    logisticInfo.put("orderId", l.get("orderId").asInt());
                    logisticInfo.put("startPortId", l.get("startPortId").asInt());
                    logisticInfo.put("endPortId", l.get("endPortId").asInt());
                    logisticInfo.put("currentPortId", l.get("currentPortId").asInt());
                    logisticInfo.put("shipId", l.get("shipId").asInt());
                    logisticInfo.put("carId", l.get("carId").asInt());
                }
                
                if (record.get("start").isNotNull()) {
                    Node start = record.get("start").asNode();
                    Map<String, Object> startPort = new HashMap<>();
                    startPort.put("id", start.get("id").asLong());
                    startPort.put("name", start.get("name").asString());
                    startPort.put("country", start.get("country").asString());
                    result.put("startPort", startPort);
                }
                
                if (record.get("end").isNotNull()) {
                    Node end = record.get("end").asNode();
                    Map<String, Object> endPort = new HashMap<>();
                    endPort.put("id", end.get("id").asLong());
                    endPort.put("name", end.get("name").asString());
                    endPort.put("country", end.get("country").asString());
                    result.put("endPort", endPort);
                }
                
                if (record.get("current").isNotNull()) {
                    Node current = record.get("current").asNode();
                    Map<String, Object> currentPort = new HashMap<>();
                    currentPort.put("id", current.get("id").asLong());
                    currentPort.put("name", current.get("name").asString());
                    currentPort.put("country", current.get("country").asString());
                    result.put("currentPort", currentPort);
                }
                
                result.put("logistic", logisticInfo);
                result.put("status", "SUCCESS");
            } else {
                result.put("status", "FAILED");
                result.put("message", "物流信息不存在");
            }
        } catch (Exception e) {
            log.warn("Failed to get logistic path from Neo4j: {}", e.getMessage());
            result.put("status", "FAILED");
            result.put("message", e.getMessage());
        }
        
        return result;
    }
}
