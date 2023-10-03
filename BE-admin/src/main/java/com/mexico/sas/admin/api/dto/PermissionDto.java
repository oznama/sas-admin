package com.mexico.sas.admin.api.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PermissionDto implements Serializable {

  private static final long serialVersionUID = 7382281630224413092L;

  private Long id;
  private String name;
  private String description;
  @JsonIgnore
  private String pathsAllowed;
  @JsonIgnore
  private Long type;
  @JsonProperty("type")
  private String typeDescription;
  private Boolean active;
  @JsonIgnore
  private Boolean visible;

}
