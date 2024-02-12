package com.mexico.sas.admin.api.service.impl;

import com.mexico.sas.admin.api.constants.CatalogKeys;
import com.mexico.sas.admin.api.constants.GeneralKeys;
import com.mexico.sas.admin.api.dto.invoice.InvoiceDto;
import com.mexico.sas.admin.api.dto.invoice.InvoiceFindDto;
import com.mexico.sas.admin.api.exception.BadRequestException;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.exception.NoContentException;
import com.mexico.sas.admin.api.i18n.I18nKeys;
import com.mexico.sas.admin.api.i18n.I18nResolver;
import com.mexico.sas.admin.api.model.Invoice;
import com.mexico.sas.admin.api.model.Order;
import com.mexico.sas.admin.api.repository.InvoiceRepository;
import com.mexico.sas.admin.api.service.CatalogService;
import com.mexico.sas.admin.api.service.InvoiceService;
import com.mexico.sas.admin.api.service.OrderService;
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
public class InvoiceServiceImpl extends LogMovementUtils implements InvoiceService {

    @Autowired
    private InvoiceRepository repository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CatalogService catalogService;

    @Override
    public void save(InvoiceDto invoiceDto) throws CustomException {
        Invoice invoice = from_M_To_N(invoiceDto, Invoice.class);
        validateSave(invoiceDto, invoice);
        try {
            log.debug("Invoice {} for order id {} to save",
                    invoiceDto.getInvoiceNum(), invoiceDto.getOrderNum());
            repository.save(invoice);
//            save(Invoice.class.getSimpleName(), invoice.getInvoiceNum(), CatalogKeys.LOG_DETAIL_INSERT,
//                    I18nResolver.getMessage(I18nKeys.LOG_GENERAL_CREATION));
            log.debug("Invoice {} created", invoice.getInvoiceNum());
        } catch (Exception e) {
            String msgError = I18nResolver.getMessage(I18nKeys.INVOICE_NOT_CREATED, invoiceDto.getInvoiceNum());
            log.error(msgError, e.getMessage());
            throw new CustomException(msgError);
        }
    }

    @Override
    public void update(String invoiceNum, InvoiceDto invoiceDto) throws CustomException {
        Invoice invoice = findEntityByInvoiceNum(invoiceNum);
        String message = ChangeBeanUtils.checkInvoice(invoice, invoiceDto, catalogService);
        log.debug("Updating invoice {} with {}, changes: {}", invoice, invoiceDto, message);
        if(!message.isEmpty()) {
            repository.save(invoice);
//            save(Invoice.class.getSimpleName(), invoice.getInvoiceNum(), CatalogKeys.LOG_DETAIL_UPDATE, message);
            log.debug("Invoice updated!");
        }
    }

    @Override
    public void deleteLogic(String invoiceNum) throws CustomException {
        log.debug("Delete logic: {}", invoiceNum);
        Invoice invoice = findEntityByInvoiceNum(invoiceNum);
        repository.deleteLogic(invoiceNum, !invoice.getEliminate() ? CatalogKeys.INVOICE_STATUS_CANCELED : CatalogKeys.INVOICE_STATUS_IN_PROCESS,
                !invoice.getEliminate(), invoice.getEliminate());
//        save(Invoice.class.getSimpleName(), invoiceNum,
//                !invoice.getEliminate() ? CatalogKeys.LOG_DETAIL_DELETE_LOGIC : CatalogKeys.LOG_DETAIL_STATUS,
//                I18nResolver.getMessage(!invoice.getEliminate() ? I18nKeys.LOG_GENERAL_DELETE : I18nKeys.LOG_GENERAL_REACTIVE));
    }

    @Override
    public InvoiceDto findByInvoiceNum(String invoiceNum) throws CustomException {
        return parseFromEntity(findEntityByInvoiceNum(invoiceNum));
    }

    @Override
    public Invoice findEntityByInvoiceNum(String invoiceNum) throws CustomException {
        return repository.findById(invoiceNum)
                .orElseThrow(() -> new NoContentException(I18nResolver.getMessage(I18nKeys.INVOICE_NOT_FOUND, invoiceNum)));
    }

    @Override
    public List<InvoiceFindDto> findByOrderNum(String orderNum) throws CustomException {
        log.debug("Finding invoices by order {}", orderNum);
        Order order = orderService.findEntityByOrderNum(orderNum);
        List<Invoice> invoices = repository.findByOrderOrderByInvoiceNumAscIssuedDateAsc(order);
        List<InvoiceFindDto> invoiceFindDtos = new ArrayList<>();
        invoices.forEach( invoice -> {
            try {
                invoiceFindDtos.add(getInvoiceFindDto(invoice));
            } catch (CustomException e) {
                log.error("Impossible add invoice {}, error: {}", invoice.getInvoiceNum(), e.getMessage());
            }
        });
        try {
            invoiceFindDtos.add(getTotal(invoices, order));
        } catch (CustomException e) {
            log.error("Impossible add invoice total, error: {}", e.getMessage());
        }
        log.debug("Invoices find {}", invoiceFindDtos.size());
        return invoiceFindDtos;
    }

    @Override
    public Page<InvoiceFindDto> findAll(String filter, Pageable pageable) {
        log.debug("Finding all Invoices");
        Page<Invoice> invoices = findByFilter(filter, null, null, null, null, null, null, pageable);
        List<InvoiceFindDto> invoiceFindDtos = new ArrayList<>();
        invoices.forEach( invoice -> {
            try {
                invoiceFindDtos.add(getInvoiceFindDto(invoice));
            } catch (CustomException e) {
                log.error("Impossible add invoice {}, error: {}", invoice.getInvoiceNum(), e.getMessage());
            }
        });
//        try {
//            invoiceFindDtos.add((getPaid(invoices.stream()
//                    .filter( i -> i.getStatus().equals(CatalogKeys.INVOICE_STATUS_PAID))
//                    .collect(Collectors.toList()))));
//            invoiceFindDtos.add(getTotal(invoices.stream()
//                    .filter( i -> !i.getStatus().equals(CatalogKeys.INVOICE_STATUS_CANCELED))
//                    .collect(Collectors.toList())));
//        } catch (CustomException e) {
//            log.error("Impossible add invoice total, error: {}", e.getMessage());
//        }
        return new PageImpl<>(invoiceFindDtos, pageable, invoices.getTotalElements());
    }

    @Override
    public InvoiceFindDto getAmountPaid(String orderNum) throws CustomException {
        Order order = orderService.findEntityByOrderNum(orderNum);
        List<Invoice> invoices = repository.findByOrderOrderByInvoiceNumAscIssuedDateAsc(order);
        return getTotal(invoices, order);
    }

    private InvoiceDto parseFromEntity(Invoice invoice) throws CustomException {
        InvoiceDto invoiceDto = from_M_To_N(invoice, InvoiceDto.class);
        invoiceDto.setOrderNum(invoice.getOrder().getOrderNum());
        invoiceDto.setIssuedDate(dateToString(invoice.getIssuedDate(), GeneralKeys.FORMAT_DDMMYYYY, true));
        invoiceDto.setPaymentDate(dateToString(invoice.getPaymentDate(), GeneralKeys.FORMAT_DDMMYYYY, true));
        return invoiceDto;
    }

    private InvoiceFindDto getInvoiceFindDto(Invoice invoice) throws CustomException {
        InvoiceFindDto invoiceFindDto = from_M_To_N(invoice, InvoiceFindDto.class);
        invoiceFindDto.setOrderNum(invoice.getOrder().getOrderNum());
        invoiceFindDto.setProjectKey(invoice.getOrder().getProject().getKey());
        invoiceFindDto.setIssuedDate(dateToString(invoice.getIssuedDate(), GeneralKeys.FORMAT_DDMMYYYY, true));
        invoiceFindDto.setPaymentDate(dateToString(invoice.getPaymentDate(), GeneralKeys.FORMAT_DDMMYYYY, true));
        invoiceFindDto.setAmountStr(formatCurrency(invoice.getAmount()));
        invoiceFindDto.setTaxStr(formatCurrency(invoice.getTax()));
        invoiceFindDto.setTotalStr(formatCurrency(invoice.getTotal()));
        return invoiceFindDto;
    }

    private InvoiceFindDto getTotal(List<Invoice> invoices, Order order) throws CustomException {
        List<Invoice> invWithoutCanceleds = invoices.stream()
                .filter( i -> !i.getStatus().equals(CatalogKeys.INVOICE_STATUS_CANCELED))
                .collect(Collectors.toList());
        BigDecimal totalAmount = invWithoutCanceleds.stream().map(pa -> pa.getAmount() ).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalTax = invWithoutCanceleds.stream().map( pa -> pa.getTax() ).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalT = invWithoutCanceleds.stream().map( pa -> pa.getTotal() ).reduce(BigDecimal.ZERO, BigDecimal::add);
        Integer totalP = invWithoutCanceleds.stream().map( pa -> pa.getPercentage() ).reduce(0, (sum, p) -> sum + p);
        InvoiceFindDto invoiceFindDto = new InvoiceFindDto();
        invoiceFindDto.setInvoiceNum(GeneralKeys.FOOTER_TOTAL);
        invoiceFindDto.setAmount(totalAmount);
        invoiceFindDto.setTax(totalTax);
        invoiceFindDto.setTotal(totalT);
        invoiceFindDto.setAmountStr(formatCurrency(totalAmount));
        invoiceFindDto.setTaxStr(formatCurrency(totalTax));
        invoiceFindDto.setTotalStr(formatCurrency(totalT));
        invoiceFindDto.setPercentage(totalP);
        BigDecimal totalPaid = invWithoutCanceleds.stream()
                .filter( i -> i.getStatus().equals(CatalogKeys.INVOICE_STATUS_PAID) )
                .map(pa -> pa.getAmount() ).reduce(BigDecimal.ZERO, BigDecimal::add);
        long canceled = invoices.stream().filter( o -> o.getStatus().equals(CatalogKeys.INVOICE_STATUS_CANCELED) ).count();
        invoiceFindDto.setStatus( totalPaid.equals(order.getAmount()) ? CatalogKeys.INVOICE_STATUS_PAID
                : ( !invoices.isEmpty() && canceled == invoices.size() ? CatalogKeys.INVOICE_STATUS_CANCELED : CatalogKeys.INVOICE_STATUS_IN_PROCESS ) );
        return invoiceFindDto;
    }

    private void validateSave(InvoiceDto invoiceDto, Invoice invoice) throws CustomException {
        invoice.setIssuedDate(stringToDate(invoiceDto.getIssuedDate(), GeneralKeys.FORMAT_DDMMYYYY));
        invoice.setPaymentDate(stringToDate(invoiceDto.getPaymentDate(), GeneralKeys.FORMAT_DDMMYYYY));
        try {
            findByInvoiceNum(invoiceDto.getInvoiceNum());
            throw new BadRequestException(I18nResolver.getMessage(I18nKeys.INVOICE_NUMBER_DUPLICATED, invoiceDto.getInvoiceNum()), null);
        } catch (CustomException e) {
            if(e instanceof BadRequestException)
                throw e;
        }
        invoice.setOrder(new Order(invoiceDto.getOrderNum()));
        invoice.setCreatedBy(getCurrentUser().getUserId());
    }

    private Page<Invoice> findByFilter(String filter, Order order, Long createdBy,
                                     Long status, Boolean active, Date issuedDate, Date paymentDate,
                                     Pageable pageable) {
        log.debug("Find by filter: {}", filter);
        return repository.findAll((Specification<Invoice>) (root, query, criteriaBuilder) ->
                getPredicateDinamycFilter(filter, order, createdBy, status, active,
                        issuedDate, paymentDate, criteriaBuilder, root), pageable);
    }

    private Predicate getPredicateDinamycFilter(String filter, Order order, Long createdBy,
                                                Long status, Boolean active, Date issuedDate, Date paymentDate,
                                                CriteriaBuilder builder, Root<Invoice> root) {
        List<Predicate> predicates = new ArrayList<>();

        if(!StringUtils.isEmpty(filter)) {
            String patternFilter = String.format(GeneralKeys.PATTERN_LIKE, filter.toLowerCase());
            Predicate pInvoiceNum = builder.like(builder.lower(root.get(Invoice.Fields.invoiceNum)), patternFilter);
            predicates.add(builder.or(pInvoiceNum));
        }

        if(order != null) {
            predicates.add(builder.equal(root.get(Invoice.Fields.order), order));
        }

        if(createdBy != null) {
            predicates.add(builder.equal(root.get(Invoice.Fields.createdBy), createdBy));
        }

        if(status != null) {
            predicates.add(builder.equal(root.get(Invoice.Fields.status), status));
        }

        if(active != null) {
            predicates.add(builder.equal(root.get(Invoice.Fields.active), active));
        }

        if(issuedDate != null) {
            predicates.add(builder.equal(root.get(Invoice.Fields.issuedDate), issuedDate));
        }

        if(paymentDate != null) {
            predicates.add(builder.equal(root.get(Invoice.Fields.paymentDate), paymentDate));
        }

        return builder.and(predicates.toArray(new Predicate[predicates.size()]));
    }
}
