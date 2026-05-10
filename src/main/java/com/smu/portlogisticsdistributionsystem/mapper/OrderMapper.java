package com.smu.portlogisticsdistributionsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smu.portlogisticsdistributionsystem.entity.Order;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface OrderMapper extends BaseMapper<Order> {

    @Select("SELECT * FROM orders WHERE order_no LIKE CONCAT('%', #{keyword}, '%') OR status LIKE CONCAT('%', #{keyword}, '%')")
    List<Order> selectByKeyword(@Param("keyword") String keyword);
}
