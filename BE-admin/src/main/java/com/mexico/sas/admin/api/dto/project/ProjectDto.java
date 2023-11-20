package com.mexico.sas.admin.api.dto.project;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;
import java.util.Date;

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
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date creationDate;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date installationDate;

}
