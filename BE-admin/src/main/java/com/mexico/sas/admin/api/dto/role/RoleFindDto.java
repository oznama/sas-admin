package com.mexico.sas.admin.api.dto.role;

import com.mexico.sas.admin.api.dto.permission.PermissionFindDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Collection;

@Getter
@Setter
@ToString
public class RoleFindDto implements Serializable {

  private Long id;
  private String name;
  private String description;
  private Collection<PermissionFindDto> permissions;
}
