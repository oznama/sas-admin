package com.mexico.sas.admin.api.dto.company;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class CompanyPaggeableDto implements Serializable {

    private static final long serialVersionUID = 6290099330372833723L;
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
    private String ext;
    private String emailDomain;
    private Long type;//new
    private Boolean active;
}
