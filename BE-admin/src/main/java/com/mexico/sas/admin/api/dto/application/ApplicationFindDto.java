package com.mexico.sas.admin.api.dto.application;

import com.mexico.sas.admin.api.model.Company;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
public class ApplicationFindDto {

    private String name;
    private String description;
    private Boolean active;
    private Boolean eliminate;
    private Date creationDate;
    private Long createdBy;
    private Long companyId;
    private String companyName;
}
