package com.mexico.sas.admin.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class ClientFindSelectDto implements Serializable {
    private Long id;
    @JsonProperty("value")
    private String name;

    private List<EmployeeFindSelectDto> employess;
}
