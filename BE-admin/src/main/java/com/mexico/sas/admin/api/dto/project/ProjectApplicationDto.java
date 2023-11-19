package com.mexico.sas.admin.api.dto.project;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mexico.sas.admin.api.dto.log.LogMovementDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
public class ProjectApplicationDto {

    private Long id;
    private Long projectId;
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

    private List<LogMovementDto> history;
}
