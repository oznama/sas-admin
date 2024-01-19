package com.mexico.sas.admin.api.dto.company;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class CompanyFindDto implements Serializable {
    private static final long serialVersionUID = -7522084241926804325L;

    private Long id;
    private String name;
    private String emailDomain;
    private Long type;
}
