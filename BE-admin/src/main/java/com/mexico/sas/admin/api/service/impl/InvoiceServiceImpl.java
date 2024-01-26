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
import com.mexico.sas.admin.api.service.InvoiceService;
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
import java.util.stream.Collectors;

@Slf4j
@Service
public class InvoiceServiceImpl extends LogMovementUtils implements InvoiceService {

    @Autowired
    private InvoiceRepository repository;

    @Override
    public void save(InvoiceDto invoiceDto) throws CustomException {
        Invoice invoice = from_M_To_N(invoiceDto, Invoice.class);
        validateSave(invoiceDto, invoice);
        try {
            log.debug("Invoice {} for order id {} to save",
                    invoiceDto.getInvoiceNum(), invoiceDto.getOrderId());
            repository.save(invoice);
            save(Invoice.class.getSimpleName(), invoice.getId(), CatalogKeys.LOG_DETAIL_INSERT,
                    I18nResolver.getMessage(I18nKeys.LOG_GENERAL_CREATION));
            invoiceDto.setId(invoice.getId());
            log.debug("Invoice with id {} created", invoice.getId());
        } catch (Exception e) {
            String msgError = I18nResolver.getMessage(I18nKeys.INVOICE_NOT_CREATED, invoiceDto.getInvoiceNum());
            log.error(msgError, e.getMessage());
            throw new CustomException(msgError);
        }
    }

    @Override
    public void update(Long invoiceId, InvoiceDto invoiceDto) throws CustomException {
        Invoice invoice = findEntityById(invoiceId);
        String message = ChangeBeanUtils.checkInvoice(invoice, invoiceDto);
        log.debug("Updating invoice {} with {}, changes: {}", invoice, invoiceDto, message);
        if(!message.isEmpty()) {
            repository.save(invoice);
            save(Invoice.class.getSimpleName(), invoice.getId(), CatalogKeys.LOG_DETAIL_UPDATE, message);
            log.debug("Invoice updated!");
        }
    }

    @Override
    public InvoiceDto findById(Long id) throws CustomException {
        return parseFromEntity(findEntityById(id));
    }

    @Override
    public Invoice findEntityById(Long id) throws CustomException {
        return repository.findById(id)
                .orElseThrow(() -> new NoContentException(I18nResolver.getMessage(I18nKeys.INVOICE_NOT_FOUND, id)));
    }

    @Override
    public InvoiceDto findByInvoiceNum(String invoiceNum) throws CustomException {
        return parseFromEntity(repository.findByInvoiceNum(invoiceNum)
                .orElseThrow(() -> new NoContentException(I18nResolver.getMessage(I18nKeys.INVOICE_NUMBER_NOT_FOUND, invoiceNum))));
    }

    @Override
    public List<InvoiceFindDto> findByOrderId(Long orderId) throws CustomException {
        log.debug("Finding invoices by order {}", orderId);
        List<Invoice> invoices = repository.findByOrder(new Order(orderId));
        List<InvoiceFindDto> invoiceFindDtos = new ArrayList<>();
        invoices.forEach( invoice -> {
            try {
                invoiceFindDtos.add(getInvoiceFindDto(invoice));
            } catch (CustomException e) {
                log.error("Impossible add invoice {}, error: {}", invoice.getId(), e.getMessage());
            }
        });
        try {
            invoiceFindDtos.add((getPaid(invoices.stream()
                    .filter( i -> i.getStatus().equals(CatalogKeys.INVOICE_STATUS_PAID))
                    .collect(Collectors.toList()))));
            invoiceFindDtos.add(getTotal(invoices));
        } catch (CustomException e) {
            log.error("Impossible add invoice total, error: {}", e.getMessage());
        }
        log.debug("Invoices find {}", invoiceFindDtos.size());
        return invoiceFindDtos;
    }

    @Override
    public Page<InvoiceFindDto> findAll(String filter, Pageable pageable) {
        log.debug("Finding all Invoices");
        List<Invoice> invoices = repository.findAll();
        List<InvoiceFindDto> invoiceFindDtos = new ArrayList<>();
        invoices.forEach( invoice -> {
            try {
                invoiceFindDtos.add(getInvoiceFindDto(invoice));
            } catch (CustomException e) {
                log.error("Impossible add invoice {}, error: {}", invoice.getId(), e.getMessage());
            }
        });
        try {
            invoiceFindDtos.add((getPaid(invoices.stream()
                    .filter( i -> i.getStatus().equals(CatalogKeys.INVOICE_STATUS_PAID))
                    .collect(Collectors.toList()))));
            invoiceFindDtos.add(getTotal(invoices));
        } catch (CustomException e) {
            log.error("Impossible add invoice total, error: {}", e.getMessage());
        }
        log.debug("Invoices find {}", invoiceFindDtos.size());
        return new PageImpl<>(invoiceFindDtos, pageable, repository.count());
    }

    private InvoiceDto parseFromEntity(Invoice invoice) throws CustomException {
        InvoiceDto invoiceDto = from_M_To_N(invoice, InvoiceDto.class);
        invoiceDto.setOrderId(invoice.getOrder().getId());
        invoiceDto.setIssuedDate(dateToString(invoice.getIssuedDate(), GeneralKeys.FORMAT_DDMMYYYY, true));
        invoiceDto.setPaymentDate(dateToString(invoice.getPaymentDate(), GeneralKeys.FORMAT_DDMMYYYY, true));
        return invoiceDto;
    }

    private InvoiceFindDto getInvoiceFindDto(Invoice invoice) throws CustomException {
        InvoiceFindDto invoiceFindDto = from_M_To_N(invoice, InvoiceFindDto.class);
        invoiceFindDto.setOrderId(invoice.getOrder().getId());
        invoiceFindDto.setProjectId(invoice.getOrder().getProject().getId());
        invoiceFindDto.setIssuedDate(dateToString(invoice.getIssuedDate(), GeneralKeys.FORMAT_DDMMYYYY, true));
        invoiceFindDto.setPaymentDate(dateToString(invoice.getPaymentDate(), GeneralKeys.FORMAT_DDMMYYYY, true));
        invoiceFindDto.setAmountStr(formatCurrency(invoice.getAmount().doubleValue()));
        invoiceFindDto.setTaxStr(formatCurrency(invoice.getTax().doubleValue()));
        invoiceFindDto.setTotalStr(formatCurrency(invoice.getTotal().doubleValue()));
        return invoiceFindDto;
    }

    private InvoiceFindDto getPaid(List<Invoice> invoices) throws CustomException {
        BigDecimal totalAmount = invoices.stream().map(pa -> pa.getAmount() ).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalTax = invoices.stream().map( pa -> pa.getTax() ).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalT = invoices.stream().map( pa -> pa.getTotal() ).reduce(BigDecimal.ZERO, BigDecimal::add);
        InvoiceFindDto invoiceFindDto = new InvoiceFindDto();
        invoiceFindDto.setInvoiceNum(GeneralKeys.ROW_TOTAL);
        invoiceFindDto.setAmount(totalAmount);
        invoiceFindDto.setTax(totalTax);
        invoiceFindDto.setTotal(totalT);
        invoiceFindDto.setAmountStr(formatCurrency(totalAmount.doubleValue()));
        invoiceFindDto.setTaxStr(formatCurrency(totalTax.doubleValue()));
        invoiceFindDto.setTotalStr(formatCurrency(totalT.doubleValue()));
        return invoiceFindDto;
    }

    private InvoiceFindDto getTotal(List<Invoice> invoices) throws CustomException {
        BigDecimal totalAmount = invoices.stream().map(pa -> pa.getAmount() ).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalTax = invoices.stream().map( pa -> pa.getTax() ).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalT = invoices.stream().map( pa -> pa.getTotal() ).reduce(BigDecimal.ZERO, BigDecimal::add);
        Integer totalP = invoices.stream().map( pa -> pa.getPercentage() ).reduce(0, (sum, p) -> sum + p);
//        Long pending = invoices.stream().filter( pa -> pa.getStatus().equals(CatalogKeys.INVOICE_STATUS_PENDING) ).count();
//        Long paid = invoices.stream().filter( pa -> pa.getStatus().equals(CatalogKeys.INVOICE_STATUS_PAID) ).count();
//        Long canceled = invoices.stream().filter( pa -> pa.getStatus().equals(CatalogKeys.INVOICE_STATUS_CANCELED) ).count();
        InvoiceFindDto invoiceFindDto = new InvoiceFindDto();
        invoiceFindDto.setInvoiceNum(GeneralKeys.FOOTER_TOTAL);
        invoiceFindDto.setAmountStr(formatCurrency(totalAmount.doubleValue()));
        invoiceFindDto.setTaxStr(formatCurrency(totalTax.doubleValue()));
        invoiceFindDto.setTotalStr(formatCurrency(totalT.doubleValue()));
        invoiceFindDto.setPercentage(totalP);
//        invoiceFindDto.setStatus(canceled > 0 ? CatalogKeys.INVOICE_STATUS_CANCELED : (
//                pending > 0 ? CatalogKeys.INVOICE_STATUS_PENDING :
//                        (paid == invoices.size() ? CatalogKeys.INVOICE_STATUS_PAID : null)
//                ));
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
        invoice.setOrder(new Order(invoiceDto.getOrderId()));
        invoice.setCreatedBy(getCurrentUser().getUserId());
    }
}
