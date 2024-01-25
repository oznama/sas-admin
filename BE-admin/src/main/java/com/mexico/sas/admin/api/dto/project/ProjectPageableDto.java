package com.mexico.sas.admin.api.dto.project;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class ProjectPageableDto implements Serializable {

    private static final long serialVersionUID = 1344296518161744919L;

    private Long id;
    private String key;
    private String description;
    private Long status;
    private String createdBy;
    private String creationDate;
    private String company;
    private String projectManager;
    private String installationDate;
    private String amount;
    private String tax;
    private String total;
    private Boolean active;
}
