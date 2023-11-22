package com.mexico.sas.admin.api.dto.project;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;

@Getter
@Setter
@ToString
@FieldNameConstants
public class ProjectDto implements Serializable {
    private static final long serialVersionUID = 6304036644034478431L;

    private Long id;
    private String key;
    private String description;
    private Long companyId;
    private Long projectManagerId;
    private String creationDate;
    private String installationDate;

}
