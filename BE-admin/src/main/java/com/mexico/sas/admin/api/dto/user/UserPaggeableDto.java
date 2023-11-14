package com.mexico.sas.admin.api.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mexico.sas.admin.api.dto.permission.PermissionDto;
import com.mexico.sas.admin.api.dto.role.RoleDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
public class UserPaggeableDto implements Serializable {

  private static final long serialVersionUID = -3672986088951196795L;

  private Long id;
  private String name;
  private String surname;
  private String secondSurname;
  private String email;
  private String phone;
  private String image;
  private RoleDto role;
  private Date lastSession;
  private Boolean active;
  @JsonIgnore
  private List<PermissionDto> actions;

}
