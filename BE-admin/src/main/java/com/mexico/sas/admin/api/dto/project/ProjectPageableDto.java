package com.mexico.sas.admin.api.dto.project;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

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
    @JsonFormat(pattern="dd/MM/yyyy")
    private Date creationDate;
    private String company;
    private String projectManager;
    @JsonFormat(pattern="dd/MM/yyyy")
    private Date installationDate;
}
