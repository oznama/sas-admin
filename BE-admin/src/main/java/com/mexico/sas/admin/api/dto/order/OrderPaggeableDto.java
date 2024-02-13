package com.mexico.sas.admin.api.dto.order;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
public class OrderPaggeableDto implements Serializable {

    private static final long serialVersionUID = 7487435623797834423L;

    private String orderNum;
    private String projectKey;
    private String orderDate;
    private Long status;
    private BigDecimal amount;
    private BigDecimal tax;
    private BigDecimal total;
    private BigDecimal amountPaid;
    private BigDecimal taxPaid;
    private BigDecimal totalPaid;
    private String amountStr;
    private String taxStr;
    private String totalStr;
    private String amountPaidStr;
    private String taxPaidStr;
    private String totalPaidStr;
    private Boolean active;
    private Boolean eliminate;
    private String requisition;
    private String requisitionDate;
    private Long requisitionStatus;

}
