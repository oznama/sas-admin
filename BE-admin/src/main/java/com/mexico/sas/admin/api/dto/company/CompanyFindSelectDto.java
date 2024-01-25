package com.mexico.sas.admin.api.dto.company;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mexico.sas.admin.api.dto.employee.EmployeeFindSelectDto;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class CompanyFindSelectDto implements Serializable {
    private static final long serialVersionUID = 4709531127736236259L;

    private Long id;
    @JsonProperty("value")
    private String name;
    private String emailDomain;
    private Long type;

    private List<EmployeeFindSelectDto> employess;
}
