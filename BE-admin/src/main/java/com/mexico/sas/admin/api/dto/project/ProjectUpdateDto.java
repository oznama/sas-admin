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
public class ProjectUpdateDto implements Serializable {
    private static final long serialVersionUID = 1853358774885051071L;

    private Long id;
    private String key;
    private String description;
    private Long companyId;
    private Long projectManagerId;
    private String installationDate;
    private BigDecimal amount;
    private BigDecimal tax;
    private BigDecimal total;

}
