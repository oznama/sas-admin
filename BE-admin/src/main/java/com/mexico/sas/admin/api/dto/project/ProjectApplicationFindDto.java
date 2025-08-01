package com.mexico.sas.admin.api.dto.project;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ProjectApplicationFindDto implements Serializable {
    private static final long serialVersionUID = 2997336432117891262L;

    private Long id;
    private String projectKey;
    private String application;
    private String amount;
    private String tax;
    private String total;
    private Integer hours;
    private String startDate;
    private String designDate;
    private Long designStatus;
    private String developmentDate;
    private Long developmentStatus;
    private String endDate;
    private Long endStatus;
    private Boolean active;
    private Boolean eliminate;
    private Long userId;
    private String creationDate;
    private String leader;
    private String developer;
    private String observations;

}
