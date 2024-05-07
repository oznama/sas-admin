package com.mexico.sas.admin.api.dto.user;

import com.mexico.sas.admin.api.dto.employee.EmployeeFindDto;
import com.mexico.sas.admin.api.dto.role.RoleDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class UserFindDto implements Serializable {

  private static final long serialVersionUID = 973579655714936807L;

  private Long id;
  private RoleDto role;
  private Boolean active;

  private EmployeeFindDto employee;

}
