package com.mexico.sas.admin.api.dto.order;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class OrderDto implements Serializable {

    private static final long serialVersionUID = -8677175988409906686L;

    private String orderNum;
    private String projectKey;
    private String orderDate;
    private Long status;
    private BigDecimal amount;
    private BigDecimal tax;
    private BigDecimal total;
    private String requisition;
    private String requisitionDate;
    private Long requisitionStatus;
    private String observations;
}
