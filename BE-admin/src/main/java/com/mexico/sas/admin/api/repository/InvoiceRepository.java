package com.mexico.sas.admin.api.repository;

import com.mexico.sas.admin.api.model.Invoice;
import com.mexico.sas.admin.api.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByInvoiceNum(String invoiceNum);
    List<Invoice> findByOrder(Order order);

}
