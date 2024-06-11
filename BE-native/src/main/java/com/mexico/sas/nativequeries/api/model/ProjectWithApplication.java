package com.mexico.sas.nativequeries.api.model;

import lombok.*;

@Data
@AllArgsConstructor
@ToString
public class ProjectWithApplication {

    private String projectKey;
    private String projectName;
    private String pmMail;
    private String pmName;
    private String bossMail;
    private String bossName;
    private Long numOrders;
    private String projectAmount;
    private String tax;
    private String total;
    private String installationDate;
    private String monitoringDate;
}
