package com.mexico.sas.admin.api.dto.application;

import com.mexico.sas.admin.api.model.Company;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
@Getter
@Setter
@ToString
public class ApplicationUpdateDto {
    private String description;
    private Long companyId;
}
