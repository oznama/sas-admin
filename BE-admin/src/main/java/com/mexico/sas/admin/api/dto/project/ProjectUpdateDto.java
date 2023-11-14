package com.mexico.sas.admin.api.dto.project;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@ToString
@FieldNameConstants
public class ProjectUpdateDto implements Serializable {
    private Long id;
    private String description;
    private Long clientId;
    private Long projectManagerId;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date installationDate;

}
