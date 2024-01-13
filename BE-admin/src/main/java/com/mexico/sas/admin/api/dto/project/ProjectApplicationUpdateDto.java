package com.mexico.sas.admin.api.dto.project;

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
public class ProjectApplicationUpdateDto implements Serializable {

    private static final long serialVersionUID = 1277148987583745666L;

    private Long id;
    private Long applicationId;
    private BigDecimal amount;
    private BigDecimal tax;
    private BigDecimal total;
    private Integer hours;
    private Long leaderId;
    private Long developerId;
    private String startDate;
    private String designDate;
    private String developmentDate;
    private String endDate;
    private String requisition;
    private String requisitionDate;
}
