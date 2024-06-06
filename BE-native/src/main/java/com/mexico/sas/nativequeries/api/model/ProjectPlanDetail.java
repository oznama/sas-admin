package com.mexico.sas.nativequeries.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class ProjectPlanDetail {
    private String appName;
    private String leader;
    private String leaderMail;
    private String developer;
    private String developerMail;
    private String startDate;
    private String designDate;
    private String developmentDate;
    private String endDate;
}
