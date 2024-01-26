package com.mexico.sas.admin.api.service.impl;

import com.mexico.sas.admin.api.constants.CatalogKeys;
import com.mexico.sas.admin.api.constants.GeneralKeys;
import com.mexico.sas.admin.api.dto.invoice.InvoiceFindDto;
import com.mexico.sas.admin.api.dto.order.OrderDto;
import com.mexico.sas.admin.api.dto.order.OrderFindDto;
import com.mexico.sas.admin.api.exception.BadRequestException;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.exception.NoContentException;
import com.mexico.sas.admin.api.i18n.I18nKeys;
import com.mexico.sas.admin.api.i18n.I18nResolver;
import com.mexico.sas.admin.api.model.Order;
import com.mexico.sas.admin.api.model.Project;
import com.mexico.sas.admin.api.repository.OrderRepository;
import com.mexico.sas.admin.api.service.InvoiceService;
import com.mexico.sas.admin.api.service.OrderService;
import com.mexico.sas.admin.api.util.ChangeBeanUtils;
import com.mexico.sas.admin.api.util.LogMovementUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderServiceImpl extends LogMovementUtils implements OrderService {

    @Autowired
    private OrderRepository repository;

    @Autowired
    private InvoiceService invoiceService;

    @Override
    public void save(OrderDto orderDto) throws CustomException {
        Order order = from_M_To_N(orderDto, Order.class);
        validateSave(orderDto, order);
        try {
            log.debug("Order {} for project id {} to save",
                    orderDto.getOrderNum(), orderDto.getProjectId());
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
    public OrderDto findById(Long id) throws CustomException {
        return parseFromEntity(findEntityById(id));
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
    public List<OrderFindDto> findByProjectId(Long projectId) throws CustomException {
        log.debug("Finding Orders by proyect {}", projectId);
        List<Order> orders = repository.findByProject(new Project(projectId));
        List<OrderFindDto> ordersFindDto = new ArrayList<>();
        orders.forEach( order -> {
            try {
                ordersFindDto.add(getOrderFindDto(order));
            } catch (CustomException e) {
                log.error("Impossible add order {}, error: {}", order.getId(), e.getMessage());
            }
        });
        try {
            ordersFindDto.add(getTotal(orders));
        } catch (CustomException e) {
            log.error("Impossible add order total, error: {}", e.getMessage());
        }
        return ordersFindDto;
    }

    @Override
    public Page<OrderFindDto> findAll(String filter, Pageable pageable) {
        log.debug("Finding all Orders");
        List<Order> orders = repository.findAll();
        List<OrderFindDto> ordersFindDto = new ArrayList<>();
        orders.forEach( order -> {
            try {
                ordersFindDto.add(getOrderFindDto(order));
            } catch (CustomException e) {
                log.error("Impossible add order {}, error: {}", order.getId(), e.getMessage());
            }
        });
        try {
            ordersFindDto.add(getTotal(orders));
        } catch (CustomException e) {
            log.error("Impossible add order total, error: {}", e.getMessage());
        }
        return new PageImpl<>(ordersFindDto, pageable, repository.count());
    }

    private OrderDto parseFromEntity(Order order) throws CustomException {
        OrderDto orderDto = from_M_To_N(order, OrderDto.class);
        orderDto.setProjectId(order.getProject().getId());
        orderDto.setOrderDate(dateToString(order.getOrderDate(), GeneralKeys.FORMAT_DDMMYYYY, true));
        orderDto.setRequisitionDate(dateToString(order.getRequisitionDate(), GeneralKeys.FORMAT_DDMMYYYY, true));
        return orderDto;
    }

    private OrderFindDto getOrderFindDto(Order order) throws CustomException {
        log.debug("Parsing order to orderFindDto");
        OrderFindDto orderFindDto = from_M_To_N(order, OrderFindDto.class);
        orderFindDto.setProjectId(order.getProject().getId());
        orderFindDto.setOrderDate(dateToString(order.getOrderDate(), GeneralKeys.FORMAT_DDMMYYYY, true));
        orderFindDto.setRequisitionDate(dateToString(order.getRequisitionDate(), GeneralKeys.FORMAT_DDMMYYYY, true));
        orderFindDto.setAmount(formatCurrency(order.getAmount().doubleValue()));
        orderFindDto.setTax(formatCurrency(order.getTax().doubleValue()));
        orderFindDto.setTotal(formatCurrency(order.getTotal().doubleValue()));
        List<BigDecimal> amounts = setAmountPaid(order.getId());
        orderFindDto.setAmountPaid(formatCurrency(amounts.get(0).doubleValue()));
        orderFindDto.setTaxPaid(formatCurrency(amounts.get(1).doubleValue()));
        orderFindDto.setTotalPaid(formatCurrency(amounts.get(2).doubleValue()));
        return orderFindDto;
    }

    private OrderFindDto getTotal(List<Order> orders) throws CustomException {
        BigDecimal totalAmount = orders.stream().map(pa -> pa.getAmount() ).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalTax = orders.stream().map( pa -> pa.getTax() ).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalT = orders.stream().map( pa -> pa.getTotal() ).reduce(BigDecimal.ZERO, BigDecimal::add);
        OrderFindDto orderFindDto = new OrderFindDto();
        orderFindDto.setOrderNum(GeneralKeys.FOOTER_TOTAL);
        orderFindDto.setAmount(formatCurrency(totalAmount.doubleValue()));
        orderFindDto.setTax(formatCurrency(totalTax.doubleValue()));
        orderFindDto.setTotal(formatCurrency(totalT.doubleValue()));
        totalAmount = BigDecimal.ZERO;
        totalTax = BigDecimal.ZERO;
        totalT = BigDecimal.ZERO;
        for(Order order : orders) {
            List<BigDecimal> amounts = setAmountPaid(order.getId());
            totalAmount.add(amounts.get(0));
            totalTax.add(amounts.get(1));
            totalT.add(amounts.get(2));
        }
        orderFindDto.setAmountPaid(formatCurrency(totalAmount.doubleValue()));
        orderFindDto.setTaxPaid(formatCurrency(totalTax.doubleValue()));
        orderFindDto.setTotalPaid(formatCurrency(totalT.doubleValue()));
        return orderFindDto;
    }

    private List<BigDecimal> setAmountPaid(Long orderId) throws CustomException {
        List<BigDecimal> amounts = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;
        BigDecimal totalT = BigDecimal.ZERO;
        Optional<InvoiceFindDto> invoicePaid = invoiceService.findByOrderId(orderId)
                .stream()
                .filter( i -> i.getInvoiceNum().equals(GeneralKeys.ROW_TOTAL))
                .findFirst();
        if( invoicePaid.isPresent() ) {
            totalAmount = invoicePaid.get().getAmount() != null ? invoicePaid.get().getAmount() : BigDecimal.ZERO;
            totalTax = invoicePaid.get().getTax() != null ? invoicePaid.get().getTax() : BigDecimal.ZERO;
            totalT = invoicePaid.get().getTotal() != null ? invoicePaid.get().getTotal() : BigDecimal.ZERO;
        }
        amounts.add(totalAmount);
        amounts.add(totalTax);
        amounts.add(totalT);
        return amounts;
    }

    private void validateSave(OrderDto orderDto, Order order) throws CustomException {
        order.setOrderDate(stringToDate(orderDto.getOrderDate(), GeneralKeys.FORMAT_DDMMYYYY));
        order.setRequisitionDate(stringToDate(orderDto.getRequisitionDate(), GeneralKeys.FORMAT_DDMMYYYY));
        try {
            findByOrderNum(orderDto.getOrderNum());
            throw new BadRequestException(I18nResolver.getMessage(I18nKeys.ORDER_NUMBER_DUPLICATED, orderDto.getOrderNum()), null);
        } catch (CustomException e) {
            if(e instanceof BadRequestException)
                throw e;
        }
        order.setProject(new Project(orderDto.getProjectId()));
        order.setCreatedBy(getCurrentUser().getUserId());
    }
}
