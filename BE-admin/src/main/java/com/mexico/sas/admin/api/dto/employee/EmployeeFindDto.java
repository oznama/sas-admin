package com.mexico.sas.admin.api.dto.employee;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@ToString
public class EmployeeFindDto implements Serializable {
    private Long id;
    private String name;
    private String secondName;
    private String surname;
    private String secondSurname;
    private Boolean active;
    private Boolean eliminate;
    private Date creationDate;
    private Long userId;
    private Long clientId;
}
