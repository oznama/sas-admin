package com.mexico.sas.admin.api.dto;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RolePermissionDto implements Serializable {

  private static final long serialVersionUID = 2879080659353035702L;

  private Long id;
  private Long permissionId;
  private Long roleId;

}
