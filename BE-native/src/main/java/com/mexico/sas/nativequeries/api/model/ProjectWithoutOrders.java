package com.mexico.sas.nativequeries.api.model;

import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@ToString
public class ProjectWithoutOrders {

    private String projectKey;
    private String projectName;
    private String pmMail;
    private String pmName;
    private Long numOrders;
    private BigDecimal projectAmount;

}
