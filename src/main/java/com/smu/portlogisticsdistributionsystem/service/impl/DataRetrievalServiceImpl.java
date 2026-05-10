package com.smu.portlogisticsdistributionsystem.service.impl;

import com.alibaba.fastjson.JSON;
import com.smu.portlogisticsdistributionsystem.entity.*;
import com.smu.portlogisticsdistributionsystem.mapper.*;
import com.smu.portlogisticsdistributionsystem.service.DataRetrievalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据检索服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DataRetrievalServiceImpl implements DataRetrievalService {

    private final PortMapper portMapper;
    private final ShipMapper shipMapper;
    private final CarMapper carMapper;
    private final LogisticsMapper logisticsMapper;
    private final ContainerMapper containerMapper;
    private final CompanyMapper companyMapper;
    private final OrderMapper orderMapper;

    @Override
    public String retrievePorts(String query) {
        List<Port> ports = portMapper.selectList(null);
        return formatResult("港口信息", ports);
    }

    @Override
    public String retrieveShips(String query) {
        List<Ship> ships = shipMapper.selectList(null);
        return formatResult("船舶信息", ships);
    }

    @Override
    public String retrieveCars(String query) {
        List<Car> cars = carMapper.selectList(null);
        return formatResult("车辆信息", cars);
    }

    @Override
    public String retrieveLogistics(String query) {
        List<Logistics> logistics = logisticsMapper.selectList(null);
        return formatResult("物流信息", logistics);
    }

    @Override
    public String retrieveContainers(String query) {
        List<Container> containers = containerMapper.selectList(null);
        return formatResult("集装箱信息", containers);
    }

    @Override
    public String retrieveCompanies(String query) {
        List<Company> companies = companyMapper.selectList(null);
        return formatResult("公司信息", companies);
    }

    @Override
    public String retrieveOrders(String query) {
        List<Order> orders = orderMapper.selectList(null);
        return formatResult("订单信息", orders);
    }

    @Override
    public String smartRetrieve(String query) {
        if (!StringUtils.hasText(query)) {
            return "{}";
        }

        String result;

        // 精确匹配：只返回用户明确请求的单一数据类型
        if (query.contains("车辆")) {
            result = retrieveCars(query);
            if (!result.equals("{}")) {
                return "{\"cars\":" + result + "}";
            }
        }

        if (query.contains("船舶")) {
            result = retrieveShips(query);
            if (!result.equals("{}")) {
                return "{\"ships\":" + result + "}";
            }
        }

        if (query.contains("港口") || query.contains("泊位") || query.contains("码头")) {
            result = retrievePorts(query);
            if (!result.equals("{}")) {
                return "{\"ports\":" + result + "}";
            }
        }

        if (query.contains("集装箱")) {
            result = retrieveContainers(query);
            if (!result.equals("{}")) {
                return "{\"containers\":" + result + "}";
            }
        }

        if (query.contains("物流") || query.contains("运输")) {
            result = retrieveLogistics(query);
            if (!result.equals("{}")) {
                return "{\"logistics\":" + result + "}";
            }
        }

        if (query.contains("公司") || query.contains("企业")) {
            result = retrieveCompanies(query);
            if (!result.equals("{}")) {
                return "{\"companies\":" + result + "}";
            }
        }

        if (query.contains("订单")) {
            result = retrieveOrders(query);
            if (!result.equals("{}")) {
                return "{\"orders\":" + result + "}";
            }
        }

        // 如果没有匹配到具体类型，返回空
        return "{}";
    }

    private String formatResult(String type, List<?> data) {
        if (data == null || data.isEmpty()) {
            return "{}";
        }
        Map<String, Object> result = new HashMap<>();
        result.put("type", type);
        result.put("count", data.size());
        result.put("data", data);
        return JSON.toJSONString(result);
    }
}
