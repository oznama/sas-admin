package com.mexico.sas.admin.api.dto.user;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mexico.sas.admin.api.dto.permission.PermissionDto;
import com.mexico.sas.admin.api.dto.role.RoleDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
@FieldNameConstants
public class UserDto implements Serializable {

  private static final long serialVersionUID = 1482940354125206599L;

  private Long id;

  @JsonIgnore
  private String password;

  private Long employeeId;

  @JsonIgnore
  private Boolean active;
  @ApiModelProperty(notes = "Roles (1 Administrador)", example = "1")
  private Long role;
  @JsonIgnore
  private RoleDto roleDto;
  @JsonIgnore
  private List<PermissionDto> permissions;

}
