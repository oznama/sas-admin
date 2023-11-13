package com.mexico.sas.admin.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class ApplicationFindDto implements Serializable {
    private Long id;
    private String application;
    private BigDecimal amount;
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
