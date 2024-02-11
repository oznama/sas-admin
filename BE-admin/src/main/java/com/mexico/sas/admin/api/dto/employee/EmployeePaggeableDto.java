package com.mexico.sas.admin.api.dto.employee;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@ToString
public class EmployeePaggeableDto implements Serializable {
    private static final long serialVersionUID = 7659626361990949868L;
    private Long id;
    private String email;
    private String name;
    private String secondName;
    private String surname;
    private String secondSurname;
    private String fullName;
    private String phone;
    private Boolean active;
    private Boolean eliminate;
    private String createdBy;
    private String creationDate;
    private String company;
    private String position;
    private String boss;
    private String cellphone;
    private String country;
    private String city;
    private String ext;
}
