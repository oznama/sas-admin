package com.mexico.sas.admin.api.service.impl;

import com.mexico.sas.admin.api.constants.CatalogKeys;
import com.mexico.sas.admin.api.constants.GeneralKeys;
import com.mexico.sas.admin.api.dto.order.OrderDto;
import com.mexico.sas.admin.api.dto.order.OrderFindDto;
import com.mexico.sas.admin.api.exception.BadRequestException;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.exception.NoContentException;
import com.mexico.sas.admin.api.i18n.I18nKeys;
import com.mexico.sas.admin.api.i18n.I18nResolver;
import com.mexico.sas.admin.api.model.Order;
import com.mexico.sas.admin.api.model.ProjectApplication;
import com.mexico.sas.admin.api.repository.OrderRepository;
import com.mexico.sas.admin.api.service.OrderService;
import com.mexico.sas.admin.api.util.ChangeBeanUtils;
import com.mexico.sas.admin.api.util.LogMovementUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class OrderServiceImpl extends LogMovementUtils implements OrderService {

    @Autowired
    private OrderRepository repository;

    @Override
    public void save(OrderDto orderDto) throws CustomException {
        Order order = from_M_To_N(orderDto, Order.class);
        validateSave(orderDto, order);
        try {
            log.debug("Order {} for project application id {} to save",
                    orderDto.getOrderNum(), orderDto.getProjectApplicationId());
            repository.save(order);
            save(Order.class.getSimpleName(), order.getId(), CatalogKeys.LOG_DETAIL_INSERT,
                    I18nResolver.getMessage(I18nKeys.LOG_GENERAL_CREATION));
            orderDto.setId(order.getId());
            log.debug("Order with id {} created", order.getId());
        } catch (Exception e) {
            String msgError = I18nResolver.getMessage(I18nKeys.ORDER_NOT_CREATED, orderDto.getOrderNum());
            log.error(msgError, e.getMessage());
            throw new CustomException(msgError);
        }
    }

    @Override
    public void update(Long orderId, OrderDto orderDto) throws CustomException {
        Order order = findEntityById(orderId);
        String message = ChangeBeanUtils.checkOrder(order, orderDto);

        if(!message.isEmpty()) {
            repository.save(order);
            save(Order.class.getSimpleName(), order.getId(), CatalogKeys.LOG_DETAIL_UPDATE, message);
            log.debug("Order updated!");
        }
    }

    @Override
    public OrderFindDto findById(Long id) throws CustomException {
        return getOrderFindDto(findEntityById(id));
    }

    @Override
    public Order findEntityById(Long id) throws CustomException {
        return repository.findById(id)
                .orElseThrow(() -> new NoContentException(I18nResolver.getMessage(I18nKeys.ORDER_NOT_FOUND, id)));
    }

    @Override
    public OrderDto findByOrderNum(String orderNum) throws CustomException {
        return parseFromEntity(repository.findByOrderNum(orderNum)
                .orElseThrow(() -> new NoContentException(I18nResolver.getMessage(I18nKeys.ORDER_NUMBER_NOT_FOUND, orderNum))));
    }

    @Override
    public List<OrderFindDto> findByProjectApplicationId(Long projectApplicationId) throws CustomException {
        List<Order> orders = repository.findByProjectApplication(new ProjectApplication(projectApplicationId));
        List<OrderFindDto> ordersFindDto = new ArrayList<>();
        orders.forEach( order -> {
            try {
                ordersFindDto.add(getOrderFindDto(order));
            } catch (CustomException e) {
                log.error("Impossible add order {}, error: {}", order.getId(), e.getMessage());
            }
        });
        return ordersFindDto;
    }

    private OrderDto parseFromEntity(Order order) throws CustomException {
        OrderDto orderDto = from_M_To_N(order, OrderDto.class);
        orderDto.setProjectApplicationId(order.getProjectApplication().getId());
        orderDto.setOrderDate(dateToString(order.getOrderDate(), GeneralKeys.FORMAT_DDMMYYYY, true));
        return orderDto;
    }

    private OrderFindDto getOrderFindDto(Order order) throws CustomException {
        OrderFindDto orderFindDto = from_M_To_N(order, OrderFindDto.class);
        orderFindDto.setOrderDate(dateToString(order.getOrderDate(), GeneralKeys.FORMAT_DDMMYYYY, true));
        return orderFindDto;
    }

    private void validateSave(OrderDto orderDto, Order order) throws CustomException {
        order.setOrderDate(stringToDate(orderDto.getOrderDate(), GeneralKeys.FORMAT_DDMMYYYY));
        try {
            findByOrderNum(orderDto.getOrderNum());
            throw new BadRequestException(I18nResolver.getMessage(I18nKeys.ORDER_NUMBER_DUPLICATED, orderDto.getOrderNum()), null);
        } catch (CustomException e) {
            if(e instanceof BadRequestException)
                throw e;
        }
        order.setCreatedBy(getCurrentUser().getUserId());
    }
}
