package com.mexico.sas.admin.api.dto.project;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ProjectApplicationPaggeableDto implements Serializable {

    private static final long serialVersionUID = 4095265061471563075L;
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
    private String designStatusDesc;
    private String developmentDate;
    private Long developmentStatus;
    private String developmentStatusDesc;
    private String endDate;
    private Long endStatus;
    private String endStatusDesc;
    private Boolean active;
    private Boolean eliminate;
    private Long userId;
    private String creationDate;
    private String leader;
    private String developer;
}
