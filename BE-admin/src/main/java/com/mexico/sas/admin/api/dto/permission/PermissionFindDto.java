package com.mexico.sas.admin.api.dto.permission;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class PermissionFindDto implements Serializable {

  private static final long serialVersionUID = 3402672704877266415L;

  private Long id;
  private String name;
  private String description;
  private Boolean active;

}
