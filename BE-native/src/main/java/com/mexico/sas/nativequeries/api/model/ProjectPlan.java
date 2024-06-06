package com.mexico.sas.nativequeries.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class ProjectPlan {
    private String appName;
    private String appDescription;
    private Boolean appNotificated;
}
