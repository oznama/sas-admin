package com.mexico.sas.admin.api.dto.project;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
public class ProjectFindDto implements Serializable {

    private static final long serialVersionUID = -8944765314269699196L;

    private Long id;
    private String key;
    private String description;
    private Long status;
    private String createdBy;
    private String creationDate;
    private String installationDate;
    private String monitoringDate;;
    private BigDecimal amount;
    private BigDecimal tax;
    private BigDecimal total;
    private String amountStr;
    private String taxStr;
    private String totalStr;
    private Long companyId;
    private Long projectManagerId;
    private String projectManager;
    private String pmEmail;
    private Boolean active;
    private String observations;

}
