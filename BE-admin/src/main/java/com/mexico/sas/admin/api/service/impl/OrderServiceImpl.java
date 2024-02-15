package com.mexico.sas.admin.api.service.impl;

import com.mexico.sas.admin.api.constants.CatalogKeys;
import com.mexico.sas.admin.api.constants.GeneralKeys;
import com.mexico.sas.admin.api.dto.SelectDto;
import com.mexico.sas.admin.api.dto.invoice.InvoiceFindDto;
import com.mexico.sas.admin.api.dto.order.OrderDto;
import com.mexico.sas.admin.api.dto.order.OrderFindDto;
import com.mexico.sas.admin.api.dto.order.OrderPaggeableDto;
import com.mexico.sas.admin.api.exception.BadRequestException;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.exception.NoContentException;
import com.mexico.sas.admin.api.i18n.I18nKeys;
import com.mexico.sas.admin.api.i18n.I18nResolver;
import com.mexico.sas.admin.api.model.Order;
import com.mexico.sas.admin.api.model.Project;
import com.mexico.sas.admin.api.repository.OrderRepository;
import com.mexico.sas.admin.api.service.CatalogService;
import com.mexico.sas.admin.api.service.InvoiceService;
import com.mexico.sas.admin.api.service.OrderService;
import com.mexico.sas.admin.api.service.ProjectService;
import com.mexico.sas.admin.api.util.ChangeBeanUtils;
import com.mexico.sas.admin.api.util.LogMovementUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderServiceImpl extends LogMovementUtils implements OrderService {

    @Autowired
    private OrderRepository repository;

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private CatalogService catalogService;

    @Override
    public OrderFindDto save(OrderDto orderDto) throws CustomException {
        Order order = from_M_To_N(orderDto, Order.class);
        validateSave(orderDto, order);
        try {
            log.debug("Order {} for project {} to save",
                    orderDto.getOrderNum(), orderDto.getProjectKey());
            repository.save(order);
            save(Order.class.getSimpleName(), order.getOrderNum(), CatalogKeys.LOG_DETAIL_INSERT,
                    I18nResolver.getMessage(I18nKeys.LOG_GENERAL_CREATION));
            log.debug("Order {} created", order.getOrderNum());
            return parseFromEntity(order);
        } catch (Exception e) {
            String msgError = I18nResolver.getMessage(I18nKeys.ORDER_NOT_CREATED, orderDto.getOrderNum());
            log.error(msgError, e.getMessage());
            throw new CustomException(msgError);
        }
    }

    @Override
    public void update(String orderNum, OrderDto orderDto) throws CustomException {
        Order order = findEntityByOrderNum(orderNum);
        String message = ChangeBeanUtils.checkOrder(order, orderDto, catalogService);

        if(!message.isEmpty()) {

            repository.save(order);
            save(Order.class.getSimpleName(), order.getOrderNum(), CatalogKeys.LOG_DETAIL_UPDATE, message);
            log.debug("Order updated!");
        }
    }

    @Override
    public void deleteLogic(String orderNum) throws CustomException {
        log.debug("Delete logic: {}", orderNum);
        Order order = findEntityByOrderNum(orderNum);
        repository.deleteLogic(orderNum, !order.getEliminate() ? CatalogKeys.ORDER_STATUS_CANCELED : CatalogKeys.ORDER_STATUS_IN_PROCESS,
                !order.getEliminate(), order.getEliminate());
        save(Order.class.getSimpleName(), orderNum,
                !order.getEliminate() ? CatalogKeys.LOG_DETAIL_DELETE_LOGIC : CatalogKeys.LOG_DETAIL_STATUS,
                I18nResolver.getMessage(!order.getEliminate() ? I18nKeys.LOG_GENERAL_DELETE : I18nKeys.LOG_GENERAL_REACTIVE));
    }

    @Override
    public OrderFindDto findByOrderNum(String orderNum) throws CustomException {
        return parseFromEntity(findEntityByOrderNum(orderNum));
    }

    @Override
    public Order findEntityByOrderNum(String orderNum) throws CustomException {
        return repository.findById(orderNum)
                .orElseThrow(() -> new NoContentException(I18nResolver.getMessage(I18nKeys.ORDER_NOT_FOUND, orderNum)));
    }

    @Override
    public List<OrderPaggeableDto> findByProjectKey(String projectKey) throws CustomException {
        log.debug("Finding Orders by proyect {}", projectKey);
        Project project = projectService.findEntityByKey(projectKey);
        List<Order> orders = repository.findByProjectOrderByOrderDateDesc(project);
        List<OrderPaggeableDto> ordersFindDto = new ArrayList<>();
        orders.forEach( order -> {
            try {
                ordersFindDto.add(getOrderPaggeableDto(order));
            } catch (CustomException e) {
                log.error("Impossible add order {}, error: {}", order.getOrderNum(), e.getMessage());
            }
        });
        try {
            ordersFindDto.add(getTotal(orders, project));
        } catch (CustomException e) {
            log.error("Impossible add order total, error: {}", e.getMessage());
        }
        return ordersFindDto;
    }

    @Override
    public Page<OrderPaggeableDto> findAll(String filter, Pageable pageable) {
        log.debug("Finding all Orders");
        Page<Order> orders = findByFilter(filter, null, null, null, null, null, pageable);
        List<OrderPaggeableDto> ordersFindDto = new ArrayList<>();
        orders.forEach( order -> {
            try {
                ordersFindDto.add(getOrderPaggeableDto(order));
            } catch (CustomException e) {
                log.error("Impossible add order {}, error: {}", order.getOrderNum(), e.getMessage());
            }
        });
        return new PageImpl<>(ordersFindDto, pageable, orders.getTotalElements());
    }

    @Override
    public List<SelectDto> getForSelect() {
        return getSelect(repository.findByActiveIsTrueAndEliminateIsFalseOrderByOrderNumAsc());
    }

    @Override
    public OrderPaggeableDto getAmountPaid(String projectKey) throws CustomException {
        Project project = projectService.findEntityByKey(projectKey);
        List<Order> orders = repository.findByProjectOrderByOrderDateDesc(project);
        return getTotal(orders, project);
    }

    private OrderFindDto parseFromEntity(Order order) throws CustomException {
        OrderFindDto orderDto = from_M_To_N(order, OrderFindDto.class);
        orderDto.setProjectKey(order.getProject().getKey());
        orderDto.setOrderDate(dateToString(order.getOrderDate(), GeneralKeys.FORMAT_DDMMYYYY, true));
        orderDto.setRequisitionDate(dateToString(order.getRequisitionDate(), GeneralKeys.FORMAT_DDMMYYYY, true));
        return orderDto;
    }

    private OrderPaggeableDto getOrderPaggeableDto(Order order) throws CustomException {
        log.debug("Parsing order to orderFindDto");
        OrderPaggeableDto orderPaggeableDto = from_M_To_N(order, OrderPaggeableDto.class);
        orderPaggeableDto.setProjectKey(order.getProject().getKey());
        orderPaggeableDto.setOrderDate(dateToString(order.getOrderDate(), GeneralKeys.FORMAT_DDMMYYYY, true));
        orderPaggeableDto.setRequisitionDate(dateToString(order.getRequisitionDate(), GeneralKeys.FORMAT_DDMMYYYY, true));
        orderPaggeableDto.setAmount(order.getAmount());
        orderPaggeableDto.setTax(order.getTax());
        orderPaggeableDto.setTotal(order.getTotal());
        orderPaggeableDto.setAmountStr(formatCurrency(order.getAmount()));
        orderPaggeableDto.setTaxStr(formatCurrency(order.getTax()));
        orderPaggeableDto.setTotalStr(formatCurrency(order.getTotal()));
        List<BigDecimal> amounts = setAmountPaid(order);
        orderPaggeableDto.setAmountPaid(amounts.get(0));
        orderPaggeableDto.setTaxPaid(amounts.get(1));
        orderPaggeableDto.setTotalPaid(amounts.get(2));
        orderPaggeableDto.setAmountPaidStr(formatCurrency(amounts.get(0)));
        orderPaggeableDto.setTaxPaidStr(formatCurrency(amounts.get(1)));
        orderPaggeableDto.setTotalPaidStr(formatCurrency(amounts.get(2)));
        return orderPaggeableDto;
    }

    private OrderPaggeableDto getTotal(List<Order> orders, Project project) throws CustomException {
        OrderPaggeableDto orderPaggeableDto = new OrderPaggeableDto();
        orderPaggeableDto.setOrderNum(GeneralKeys.FOOTER_TOTAL);
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;
        BigDecimal totalT = BigDecimal.ZERO;
        BigDecimal totalAmountPaid = BigDecimal.ZERO;
        BigDecimal totalTaxPaid = BigDecimal.ZERO;
        BigDecimal totalTPaid = BigDecimal.ZERO;
        BigDecimal totalRealPaid = BigDecimal.ZERO;
        for( Order order : orders ) {
            List<BigDecimal> amounts = setAmountPaid(order);
            totalAmountPaid = totalAmountPaid.add(amounts.get(0));
            totalTaxPaid = totalTaxPaid.add(amounts.get(1));
            totalTPaid = totalTPaid.add(amounts.get(2));
            totalRealPaid = totalRealPaid.add(amounts.get(3));
            if( order.getStatus().equals(CatalogKeys.ORDER_STATUS_CANCELED) || order.getStatus().equals(CatalogKeys.ORDER_STATUS_EXPIRED)) {
                totalAmount = totalAmount.add(amounts.get(0));
                totalTax = totalTax.add(amounts.get(1));
                totalT = totalT.add(amounts.get(2));
            } else {
                totalAmount = totalAmount.add(order.getAmount() != null ? order.getAmount() : BigDecimal.ZERO);
                totalTax = totalTax.add(order.getTax() != null ? order.getTax() : BigDecimal.ZERO);
                totalT = totalT.add(order.getTotal() != null ? order.getTotal() : BigDecimal.ZERO);
            }
        }
        long canceled = orders.stream().filter( o -> o.getStatus().equals(CatalogKeys.ORDER_STATUS_CANCELED) ).count();
        // long expired = orders.stream().filter( o -> o.getStatus().equals(CatalogKeys.ORDER_STATUS_EXPIRED)).count();
        log.debug("Orders canceleds ??? {}", canceled);
        orderPaggeableDto.setAmount(totalAmount);
        orderPaggeableDto.setTax(totalTax);
        orderPaggeableDto.setTotal(totalT);
        orderPaggeableDto.setAmountStr(formatCurrency(totalAmount));
        orderPaggeableDto.setTaxStr(formatCurrency(totalTax));
        orderPaggeableDto.setTotalStr(formatCurrency(totalT));
        orderPaggeableDto.setAmountPaid(totalAmountPaid);
        orderPaggeableDto.setTaxPaid(totalTaxPaid);
        orderPaggeableDto.setTotalPaid(totalTPaid);
        orderPaggeableDto.setAmountPaidStr(formatCurrency(totalAmountPaid));
        orderPaggeableDto.setTaxPaidStr(formatCurrency(totalTaxPaid));
        orderPaggeableDto.setTotalPaidStr(formatCurrency(totalTPaid));
        orderPaggeableDto.setStatus( totalRealPaid.equals(project.getAmount()) ? CatalogKeys.ORDER_STATUS_PAID
                : ( !orders.isEmpty() && canceled == orders.size() ? CatalogKeys.ORDER_STATUS_CANCELED
                : ( !orders.isEmpty() ? CatalogKeys.ORDER_STATUS_IN_PROCESS : 0l ) ) );
        return orderPaggeableDto;
    }

    private List<BigDecimal> setAmountPaid(Order order) throws CustomException {
        List<BigDecimal> amounts = new ArrayList<>();
        List<InvoiceFindDto> invoices = invoiceService.findByOrderNum(order.getOrderNum()).stream()
                .filter( i -> !i.getInvoiceNum().equals(GeneralKeys.ROW_TOTAL)
                        && !i.getInvoiceNum().equals(GeneralKeys.FOOTER_TOTAL)
                        && !i.getStatus().equals(CatalogKeys.INVOICE_STATUS_CANCELED))
                .collect(Collectors.toList());
        BigDecimal amountPaid = invoices.stream().map(pa -> pa.getAmount() ).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalRealPaid = invoices.stream().filter(i -> i.getStatus().equals(CatalogKeys.INVOICE_STATUS_PAID))
                .map(pa -> pa.getAmount() ).reduce(BigDecimal.ZERO, BigDecimal::add);
        amounts.add(amountPaid);
        amounts.add(invoices.stream().map( pa -> pa.getTax() ).reduce(BigDecimal.ZERO, BigDecimal::add));
        amounts.add(invoices.stream().map( pa -> pa.getTotal() ).reduce(BigDecimal.ZERO, BigDecimal::add));
        amounts.add(totalRealPaid);

        OrderDto orderUpdateDto = from_M_To_N(order, OrderDto.class);
        if( !order.getStatus().equals(CatalogKeys.ORDER_STATUS_PAID) && order.getAmount().equals(totalRealPaid) ) {
            orderUpdateDto.setStatus(CatalogKeys.ORDER_STATUS_PAID);
            orderUpdateDto.setRequisitionStatus(CatalogKeys.ORDER_STATUS_PAID);
            update(order.getOrderNum(), orderUpdateDto);
        } else if ( order.getStatus().equals(CatalogKeys.ORDER_STATUS_PAID) && !order.getAmount().equals(totalRealPaid) ) {
            orderUpdateDto.setStatus(CatalogKeys.ORDER_STATUS_IN_PROCESS);
            orderUpdateDto.setRequisitionStatus(CatalogKeys.ORDER_STATUS_IN_PROCESS);
            update(order.getOrderNum(), orderUpdateDto);
        }
        return amounts;
    }

    private List<SelectDto> getSelect(List<Order> orders) {
        List<SelectDto> selectDtos = new ArrayList<>();
        orders.forEach( order -> {
            try {
                SelectDto selectDto = from_M_To_N(order, SelectDto.class);
                selectDto.setName(String.format( "%s (%s - %s)", order.getOrderNum(), order.getProject().getKey(), order.getProject().getDescription()));
                selectDto.setParentId(order.getProject().getKey());
                selectDtos.add(selectDto);
            } catch (CustomException e) {
                log.error("Impossible add order {}", order.getOrderNum());
            }
        });
        return selectDtos;
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
        order.setProject(new Project(orderDto.getProjectKey()));
        order.setCreatedBy(getCurrentUser().getUserId());
    }

    private Page<Order> findByFilter(String filter, Project project, Long createdBy,
                                       Long status, Boolean active, Date orderDate,
                                       Pageable pageable) {
        log.debug("Find by filter: {}", filter);
        return repository.findAll((Specification<Order>) (root, query, criteriaBuilder) ->
                getPredicateDinamycFilter(filter, project, createdBy, status, active,
                        orderDate, criteriaBuilder, root), pageable);
    }

    private Predicate getPredicateDinamycFilter(String filter, Project project, Long createdBy,
                                                Long status, Boolean active, Date orderDate,
                                                CriteriaBuilder builder, Root<Order> root) {
        List<Predicate> predicates = new ArrayList<>();

        if(!StringUtils.isEmpty(filter)) {
            String patternFilter = String.format(GeneralKeys.PATTERN_LIKE, filter.toLowerCase());
            Predicate pOrderNum = builder.like(builder.lower(root.get(Order.Fields.orderNum)), patternFilter);
            Predicate pRequisition = builder.like(builder.lower(root.get(Order.Fields.requisition)), patternFilter);
            predicates.add(builder.or(pOrderNum, pRequisition));
        }

        if(project != null) {
            predicates.add(builder.equal(root.get(Order.Fields.project), project));
        }

        if(createdBy != null) {
            predicates.add(builder.equal(root.get(Order.Fields.createdBy), createdBy));
        }

        if(status != null) {
            predicates.add(builder.equal(root.get(Order.Fields.status), status));
        }

        if(active != null) {
            predicates.add(builder.equal(root.get(Order.Fields.active), active));
        }

        if(orderDate != null) {
            predicates.add(builder.equal(root.get(Order.Fields.orderDate), orderDate));
        }

        return builder.and(predicates.toArray(new Predicate[predicates.size()]));
    }
}
