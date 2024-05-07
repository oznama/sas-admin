package com.mexico.sas.admin.api.dto.user;

import com.mexico.sas.admin.api.dto.employee.EmployeeDto;
import com.mexico.sas.admin.api.dto.role.RoleDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class UserPaggeableDto implements Serializable {

  private static final long serialVersionUID = -3672986088951196795L;

  private Long id;
  private EmployeeDto employee;
  private RoleDto role;
  private Boolean active;

}
