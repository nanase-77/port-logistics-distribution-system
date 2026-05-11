package com.smu.portlogisticsdistributionsystem.service;

import com.smu.portlogisticsdistributionsystem.entity.Logistic;

import java.util.List;

public interface RedisLogisticService {
    void addLogistic(Logistic logistic);
    void updateLogistic(Logistic logistic);
    void deleteLogistic(Integer logisticId);
    void clearAllLogistics();
    List<Logistic> getAllLogistics();
    Logistic getLogisticById(Integer logisticId);
}
