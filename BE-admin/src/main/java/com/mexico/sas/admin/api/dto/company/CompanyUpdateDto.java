package com.mexico.sas.admin.api.dto.company;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;

@Getter
@Setter
@ToString
@FieldNameConstants
public class CompanyUpdateDto implements Serializable {

    private static final long serialVersionUID = 3097161078391200494L;

    private Long id;
    private String name;
    private String alias;
    private String rfc;
    private String address;
    private String cp;
    private String city;
    private String state;
    private String country;
    private String phone;
    private String ext;
    private String emailDomain;
    private Long type;
    private Boolean active;
}
