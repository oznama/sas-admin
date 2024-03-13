package com.mexico.sas.admin.api.dto.project;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@ToString
@FieldNameConstants
public class ProjectDto implements Serializable {
    private static final long serialVersionUID = 6304036644034478431L;

    private String key;
    private String description;
    private Long companyId;
    private Long projectManagerId;
    private String creationDate;
    private String installationDate;
    private BigDecimal amount;
    private BigDecimal tax;
    private BigDecimal total;
    private String observations;
    private Date qaStartDate;
    private Date qaEndDate;
    private Date deliveryDate;
    private String followCode;

}
