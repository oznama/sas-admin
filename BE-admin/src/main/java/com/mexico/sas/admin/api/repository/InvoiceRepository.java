package com.mexico.sas.admin.api.repository;

import com.mexico.sas.admin.api.model.Invoice;
import com.mexico.sas.admin.api.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long>, JpaSpecificationExecutor<Invoice> {

    Optional<Invoice> findByInvoiceNum(String invoiceNum);
    List<Invoice> findByOrderOrderByInvoiceNumAscIssuedDateAsc(Order order);

    @Transactional
    @Modifying
    @Query("update Invoice i set i.eliminate = :eliminate, i.active = :active, i.status = :status where i.id = :id")
    void deleteLogic(@Param(value = "id") Long id, @Param(value = "status") Long status,
                     @Param(value = "eliminate") Boolean eliminate, @Param(value = "active") Boolean active);

}
