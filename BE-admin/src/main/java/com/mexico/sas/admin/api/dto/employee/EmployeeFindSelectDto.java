package com.mexico.sas.admin.api.dto.employee;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class EmployeeFindSelectDto implements Serializable {
    private Long id;
    @JsonProperty("value")
    private String name;
}
