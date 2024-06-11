package com.mexico.sas.nativequeries.api.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ProjectPlanData {
    private String username;
    private String password;
    private String pKey;
    private String apps;
    private String fileName;
    private MultipartFile file;
}
