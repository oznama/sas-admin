package com.mexico.sas.nativequeries.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class ProjectPlanHeader {
    private String pKey;
    private String pDescription;
    private String pmName;
    private String pmMail;
}
