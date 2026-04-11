package com.smu.portlogisticsdistributionsystem;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.smu.portlogisticsdistributionsystem.mapper")
public class PortLogisticsDistributionSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(PortLogisticsDistributionSystemApplication.class, args);
    }

}
