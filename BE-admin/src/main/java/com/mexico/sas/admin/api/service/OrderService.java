package com.mexico.sas.admin.api.service;

import com.mexico.sas.admin.api.dto.order.OrderDto;
import com.mexico.sas.admin.api.dto.order.OrderFindDto;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.model.Order;

import java.util.List;

public interface OrderService {

    void save(OrderDto orderDto) throws CustomException;
    void update(Long orderId, OrderDto orderDto) throws CustomException;
    OrderDto findById(Long id) throws CustomException;
    Order findEntityById(Long id) throws CustomException;
    OrderDto findByOrderNum(String orderNum) throws CustomException;
    List<OrderFindDto> findByProjectApplicationId(Long projectApplicationId) throws CustomException;

}
