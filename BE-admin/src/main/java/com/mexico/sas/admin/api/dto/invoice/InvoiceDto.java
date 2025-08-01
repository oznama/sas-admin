package com.mexico.sas.admin.api.dto.invoice;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class InvoiceDto implements Serializable {

    private static final long serialVersionUID = 4327559323501927316L;

    private String orderNum;
    private String invoiceNum;
    private String issuedDate;
    private String paymentDate;
    private Integer percentage;
    private Long status;
    private BigDecimal amount;
    private BigDecimal tax;
    private BigDecimal total;
    private String observations;
}
