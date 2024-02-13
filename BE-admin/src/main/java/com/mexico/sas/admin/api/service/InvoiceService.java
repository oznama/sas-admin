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
    void update(String invoiceNum, InvoiceDto invoiceDto) throws CustomException;
    void deleteLogic(String invoiceNum) throws CustomException;
    InvoiceDto findByInvoiceNum(String invoiceNum) throws CustomException;
    Invoice findEntityByInvoiceNum(String invoiceNum) throws CustomException;
    List<InvoiceFindDto> findByOrderNum(String orderNum) throws CustomException;
    Page<InvoiceFindDto> findAll(String filter, Pageable pageable);

    InvoiceFindDto getAmountPaid(String orderNum) throws CustomException;

}
