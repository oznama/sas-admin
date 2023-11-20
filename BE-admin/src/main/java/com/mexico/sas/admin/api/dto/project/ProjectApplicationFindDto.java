package com.mexico.sas.admin.api.dto.project;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mexico.sas.admin.api.dto.log.LogMovementDto;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ProjectApplicationFindDto implements Serializable {
    private static final long serialVersionUID = 2997336432117891262L;

    private Long id;
    private String application;
    private String amount;
    private Integer hours;
    @JsonFormat(pattern="dd/MM/yyyy")
    private Date designDate;
    @JsonFormat(pattern="dd/MM/yyyy")
    private Date developmentDate;
    @JsonFormat(pattern="dd/MM/yyyy")
    private Date endDate;
    private Boolean active;
    private Boolean eliminate;
    private Long userId;
    @JsonFormat(pattern="dd/MM/yyyy")
    private Date creationDate;
    private String leader;
    private String developer;

}
