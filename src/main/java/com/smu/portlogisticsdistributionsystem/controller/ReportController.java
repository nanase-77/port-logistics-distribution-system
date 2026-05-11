package com.smu.portlogisticsdistributionsystem.controller;

import com.smu.portlogisticsdistributionsystem.common.Result;
import com.smu.portlogisticsdistributionsystem.entity.Car;
import com.smu.portlogisticsdistributionsystem.entity.Container;
import com.smu.portlogisticsdistributionsystem.entity.Logistic;
import com.smu.portlogisticsdistributionsystem.entity.Order;
import com.smu.portlogisticsdistributionsystem.service.impl.CarServiceImpl;
import com.smu.portlogisticsdistributionsystem.service.impl.ContainerServiceImpl;
import com.smu.portlogisticsdistributionsystem.service.impl.LogisticServiceImpl;
import com.smu.portlogisticsdistributionsystem.service.impl.OrderServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Api(tags = "报表接口")
@RestController
@RequestMapping("/api/reports")
@CrossOrigin
public class ReportController {

    @Autowired
    private OrderServiceImpl orderService;

    @Autowired
    private LogisticServiceImpl logisticService;

    @Autowired
    private CarServiceImpl carService;

    @Autowired
    private ContainerServiceImpl containerService;

    @GetMapping
    @ApiOperation("获取综合报表数据")
    public Result<Map<String, Object>> getReportData() {
        Map<String, Object> report = new HashMap<>();
        
        List<Order> orders = orderService.list();
        List<Logistic> logistics = logisticService.list();
        List<Car> cars = carService.list();
        List<Container> containers = containerService.list();
        
        long completedOrders = orders.stream().filter(o -> "已完成".equals(o.getStatus())).count();
        long processingOrders = orders.stream().filter(o -> "进行中".equals(o.getStatus())).count();
        long completedCars = cars.stream().filter(c -> "已完成".equals(c.getStatus())).count();
        long workingCars = cars.stream().filter(c -> "运行中".equals(c.getStatus())).count();
        
        int throughput = containers.size() * 20;
        
        report.put("throughput", throughput);
        report.put("completionRate", orders.isEmpty() ? 0 : String.format("%.1f", (completedOrders * 100.0 / orders.size())));
        report.put("utilizationRate", cars.isEmpty() ? 0 : String.format("%.1f", (workingCars * 100.0 / cars.size())));
        report.put("orderCount", orders.size());
        report.put("logisticCount", logistics.size());
        report.put("carCount", cars.size());
        report.put("containerCount", containers.size());
        
        return Result.success(report);
    }

    @GetMapping("/monthly-throughput")
    @ApiOperation("获取月度吞吐量数据")
    public Result<List<Map<String, Object>>> getMonthlyThroughput() {
        List<Map<String, Object>> monthlyData = new ArrayList<>();
        
        String[] months = {"1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"};
        int[] throughputs = {1200, 1350, 1100, 1450, 1600, 1550, 1700, 1800, 1650, 1500, 1400, 1900};
        
        for (int i = 0; i < 12; i++) {
            Map<String, Object> month = new HashMap<>();
            month.put("month", months[i]);
            month.put("throughput", throughputs[i]);
            month.put("growth", i == 0 ? 0 : ((throughputs[i] - throughputs[i-1]) * 100 / throughputs[i-1]));
            monthlyData.add(month);
        }
        
        return Result.success(monthlyData);
    }

    @GetMapping("/order-statistics")
    @ApiOperation("获取订单统计数据")
    public Result<Map<String, Object>> getOrderStatistics() {
        List<Order> orders = orderService.list();
        
        long completedCount = orders.stream().filter(o -> "已完成".equals(o.getStatus())).count();
        long processingCount = orders.stream().filter(o -> "进行中".equals(o.getStatus())).count();
        long total = orders.size();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("completedCount", (int) completedCount);
        stats.put("processingCount", (int) processingCount);
        stats.put("completedRate", total == 0 ? 0 : (int) (completedCount * 100 / total));
        stats.put("processingRate", total == 0 ? 0 : (int) (processingCount * 100 / total));
        
        return Result.success(stats);
    }

    @GetMapping("/port-turnover")
    @ApiOperation("获取港口周转数据")
    public Result<List<Map<String, Object>>> getPortTurnover() {
        List<Map<String, Object>> portData = new ArrayList<>();
        
        String[] ports = {"上海港", "深圳港", "宁波港", "广州港", "青岛港"};
        int[] turnovers = {4500, 3800, 3200, 2900, 2600};
        
        for (int i = 0; i < ports.length; i++) {
            Map<String, Object> port = new HashMap<>();
            port.put("portName", ports[i]);
            port.put("turnover", turnovers[i]);
            portData.add(port);
        }
        
        return Result.success(portData);
    }

    @GetMapping("/equipment-usage")
    @ApiOperation("获取设备使用数据")
    public Result<List<Map<String, Object>>> getEquipmentUsage() {
        List<Car> cars = carService.list();
        List<Map<String, Object>> usageData = new ArrayList<>();
        
        Map<String, Object> usage = new HashMap<>();
        usage.put("total", cars.size());
        usage.put("working", cars.stream().filter(c -> "运行中".equals(c.getStatus())).count());
        usage.put("idle", cars.stream().filter(c -> "空闲".equals(c.getStatus())).count());
        usage.put("maintenance", cars.stream().filter(c -> "维护中".equals(c.getStatus())).count());
        usageData.add(usage);
        
        return Result.success(usageData);
    }

    @GetMapping("/export")
    @ApiOperation("导出报表到文件")
    public Result<String> exportReport() {
        try {
            List<Order> orders = orderService.list();
            List<Logistic> logistics = logisticService.list();
            List<Car> cars = carService.list();
            List<Container> containers = containerService.list();
            
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filePath = System.getProperty("user.dir") + "/report_" + timestamp + ".csv";
            
            try (FileWriter writer = new FileWriter(filePath)) {
                writer.write("=== 订单报表 ===\n");
                writer.write("ID,订单号,用户ID,状态,创建时间\n");
                for (Order order : orders) {
                    writer.write(String.format("%d,%s,%d,%s,%s\n",
                            order.getId(),
                            order.getOrderNumber(),
                            order.getUserId(),
                            order.getStatus(),
                            order.getCreateTime()));
                }
                
                writer.write("\n=== 物流跟踪报表 ===\n");
                writer.write("ID,订单ID,起始港口,目的港口,当前港口,船舶ID,车辆ID,创建时间\n");
                for (Logistic logistic : logistics) {
                    writer.write(String.format("%d,%d,%d,%d,%d,%d,%d,%s\n",
                            logistic.getId(),
                            logistic.getOrderId(),
                            logistic.getStartPortId(),
                            logistic.getEndPortId(),
                            logistic.getCurrentPortId(),
                            logistic.getShipId(),
                            logistic.getCarId(),
                            logistic.getCreateTime()));
                }
                
                writer.write("\n=== 车辆报表 ===\n");
                writer.write("ID,车辆名称,状态,创建时间\n");
                for (Car car : cars) {
                    writer.write(String.format("%d,%s,%s,%s\n",
                            car.getId(),
                            car.getCarName(),
                            car.getStatus(),
                            car.getCreateTime()));
                }
                
                writer.write("\n=== 集装箱报表 ===\n");
                writer.write("ID,内容,尺寸,所属公司ID,创建时间\n");
                for (Container container : containers) {
                    writer.write(String.format("%d,%s,%s,%d,%s\n",
                            container.getId(),
                            container.getContent(),
                            container.getSize(),
                            container.getCompanyId(),
                            container.getCreateTime()));
                }
            }
            
            return Result.success("报表已导出到: " + filePath);
        } catch (IOException e) {
            return Result.error("导出失败: " + e.getMessage());
        }
    }
}
