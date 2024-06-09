package com.mexico.sas.nativequeries.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class ProjectAppCat {
    private String appName;
    private String leader;
    private String developer;
}
