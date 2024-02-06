package com.mexico.sas.admin.api.dto.company;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class CompanyFindDto implements Serializable {
    private static final long serialVersionUID = -7522084241926804325L;

    private Long id;
    private String name;
    private String alias;
    private String rfc;
    private String address;
    private String interior;
    private String exterior;
    private String cp;
    private String locality;
    private String city;
    private String state;
    private String country;
    private String phone;
    private String cellphone;
    private String ext;
    private String emailDomain;
    private Long type;
    private Boolean active;
    private Date creationDate;
    private Long createdBy;
}
