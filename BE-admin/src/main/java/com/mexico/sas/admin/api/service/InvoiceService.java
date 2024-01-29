package com.mexico.sas.admin.api.service;

import com.mexico.sas.admin.api.dto.invoice.InvoiceDto;
import com.mexico.sas.admin.api.dto.invoice.InvoiceFindDto;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.model.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface InvoiceService {

    void save(InvoiceDto invoiceDto) throws CustomException;
    void update(Long invoiceId, InvoiceDto invoiceDto) throws CustomException;
    void deleteLogic(Long id) throws CustomException;
    InvoiceDto findById(Long id) throws CustomException;
    Invoice findEntityById(Long id) throws CustomException;
    InvoiceDto findByInvoiceNum(String invoiceNum) throws CustomException;
    List<InvoiceFindDto> findByOrderId(Long orderId) throws CustomException;
    Page<InvoiceFindDto> findAll(String filter, Pageable pageable);

}
