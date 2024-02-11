package com.mexico.sas.admin.api.dto.invoice;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class InvoiceFindDto implements Serializable {

    private static final long serialVersionUID = -6836948041262787205L;

    private String invoiceNum;
    private String orderNum;
    private String projectKey;
    private String issuedDate;
    private String paymentDate;
    private Integer percentage;
    private Long status;
    private BigDecimal amount;
    private BigDecimal tax;
    private BigDecimal total;
    private String amountStr;
    private String taxStr;
    private String totalStr;
    private Boolean active;
    private Boolean eliminate;

}
