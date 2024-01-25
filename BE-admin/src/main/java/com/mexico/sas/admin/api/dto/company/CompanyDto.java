package com.mexico.sas.admin.api.dto.company;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class CompanyDto implements Serializable {

    private static final long serialVersionUID = -2345797168231866946L;

    private String name;
    private String rfc;
    private String address;
    private String phone;
    private String emailDomain;
    private Long type;
}
