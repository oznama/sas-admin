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
@FieldNameConstants
public class OrderDto implements Serializable {

    private static final long serialVersionUID = -7955591922799938614L;

    private Long id;
    private Long projectId;
    private String orderNum;
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
