package com.mexico.sas.admin.api.dto.project;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mexico.sas.admin.api.dto.log.LogMovementDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
@FieldNameConstants
public class ProjectApplicationUpdateDto implements Serializable {

    private static final long serialVersionUID = 1277148987583745666L;

    private Long id;
    private Long applicationId;
    private BigDecimal amount;
    private Integer hours;
    private Long leaderId;
    private Long developerId;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date designDate;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date developmentDate;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date endDate;
}
