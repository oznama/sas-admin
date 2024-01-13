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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

        if(!message.isEmpty()) {
            repository.save(invoice);
            save(Invoice.class.getSimpleName(), invoice.getId(), CatalogKeys.LOG_DETAIL_UPDATE, message);
            log.debug("Invoice updated!");
        }
    }

    @Override
    public InvoiceFindDto findById(Long id) throws CustomException {
        return getInvoiceFindDto(findEntityById(id));
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
        List<Invoice> invoices = repository.findByOrder(new Order(orderId));
        List<InvoiceFindDto> invoiceFindDtos = new ArrayList<>();
        invoices.forEach( order -> {
            try {
                invoiceFindDtos.add(getInvoiceFindDto(order));
            } catch (CustomException e) {
                log.error("Impossible add order {}, error: {}", order.getId(), e.getMessage());
            }
        });
        return invoiceFindDtos;
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
        invoiceFindDto.setIssuedDate(dateToString(invoice.getIssuedDate(), GeneralKeys.FORMAT_DDMMYYYY, true));
        invoiceFindDto.setPaymentDate(dateToString(invoice.getPaymentDate(), GeneralKeys.FORMAT_DDMMYYYY, true));
        return invoiceFindDto;
    }

    private void validateSave(InvoiceDto invoiceDto, Invoice invoice) throws CustomException {
        invoice.setIssuedDate(stringToDate(invoiceDto.getIssuedDate(), GeneralKeys.FORMAT_DDMMYYYY));
        invoice.setPaymentDate(stringToDate(invoiceDto.getPaymentDate(), GeneralKeys.FORMAT_DDMMYYYY));
        try {
            findByInvoiceNum(invoiceDto.getInvoiceNum());
            throw new BadRequestException(I18nResolver.getMessage(I18nKeys.ORDER_NUMBER_DUPLICATED, invoiceDto.getInvoiceNum()), null);
        } catch (CustomException e) {
            if(e instanceof BadRequestException)
                throw e;
        }
        invoice.setCreatedBy(getCurrentUser().getUserId());
    }
}
