package com.mexico.sas.admin.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ProjectFindDto implements Serializable {

    private Long id;
    private String key;
    private String description;
    private Long status;
    private String createdBy;
    private Date creationDate;
    private Date installationDate;
    private Long clientId;
    private Long projectManagerId;

    private List<ApplicationFindDto> applications;

}
