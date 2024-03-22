package com.mexico.sas.admin.api.dto.project;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class ProjectApplicationUpdateDto implements Serializable {

    private static final long serialVersionUID = 1277148987583745666L;

    private Long id;
    private String projectKey;
    private String application;
    private BigDecimal amount;
    private BigDecimal tax;
    private BigDecimal total;
    private Integer hours;
    private Long leaderId;
    private Long developerId;
    private String startDate;
    private String designDate;
    private Long designStatus;
    private String developmentDate;
    private Long developmentStatus;
    private String endDate;
    private Long endStatus;
    private String observations;
}
