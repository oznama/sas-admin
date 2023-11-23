package com.mexico.sas.admin.api.dto.project;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ProjectFindDto implements Serializable {

    private static final long serialVersionUID = -8944765314269699196L;

    private Long id;
    private String key;
    private String description;
    private Long status;
    private String createdBy;
    private String creationDate;
    private String installationDate;
    private Long companyId;
    private Long projectManagerId;

}
