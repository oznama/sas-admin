package com.mexico.sas.admin.api.service;

import com.mexico.sas.admin.api.dto.SelectDto;
import com.mexico.sas.admin.api.dto.order.OrderDto;
import com.mexico.sas.admin.api.dto.order.OrderFindDto;
import com.mexico.sas.admin.api.dto.order.OrderPaggeableDto;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {

    OrderFindDto save(OrderDto orderDto) throws CustomException;
    void update(Long orderId, OrderDto orderDto) throws CustomException;
    void deleteLogic(Long id) throws CustomException;
    OrderFindDto findById(Long id) throws CustomException;
    Order findEntityById(Long id) throws CustomException;
    OrderFindDto findByOrderNum(String orderNum) throws CustomException;
    List<OrderPaggeableDto> findByProjectId(Long projectId) throws CustomException;
    Page<OrderPaggeableDto> findAll(String filter, Pageable pageable);
    List<SelectDto> getForSelect();
}
