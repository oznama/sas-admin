package com.mexico.sas.nativequeries.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class ProjectWithoutInvoices {

    private String projectKey;
    private String projectName;
    private String pmMail;
    private String pmName;
    private String bossMail;
    private String bossName;
    private Long numInvoices;
    private String projectAmount;
    private String tax;
    private String total;
    private Integer percentage;
}
