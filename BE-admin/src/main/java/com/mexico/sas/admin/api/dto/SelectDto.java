package com.mexico.sas.admin.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class SelectDto implements Serializable {

    private static final long serialVersionUID = 300386973188821805L;

    private Long id;
    @JsonProperty("value")
    private String name;
    private Long parentId;
}
