package com.mexico.sas.admin.api.dto.user;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mexico.sas.admin.api.constants.Regex;
import com.mexico.sas.admin.api.dto.permission.PermissionDto;
import com.mexico.sas.admin.api.dto.role.RoleDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@ToString
@FieldNameConstants
public class UserDto implements Serializable {

  private static final long serialVersionUID = 1482940354125206599L;

  private Long id;

  @NotBlank(message = "{validation.email.required}")
  @Email(message = "{validation.format.invalid}")
  private String email;

  @NotBlank(message = "{validation.name.required}")
  @Pattern(regexp = Regex.ONLY_LETTERS_WHIT_SPACE, message = "{validation.format.invalid} {validation.only.letter}")
  private String name;

  @NotBlank(message = "{validation.lastname.required}")
  @Pattern(regexp = Regex.ONLY_LETTERS_WHIT_SPACE, message = "{validation.format.invalid} {validation.only.letter}")
  private String surname;

  @Pattern(regexp = Regex.ONLY_LETTERS_WHIT_SPACE, message = "{validation.format.invalid} {validation.only.letter}")
  private String secondSurname;

  @NotBlank(message = "{validation.phone.required}")
  @Pattern(regexp = Regex.PHONE_NUMBER, message = "{validation.format.invalid} {validation.only.number}")
  private String phone;
  private String image;
  @JsonIgnore
  private Boolean active;
  @ApiModelProperty(notes = "Roles (1 Administrador)", example = "1")
  private Long role;
  @JsonIgnore
  private RoleDto roleDto;
  @JsonIgnore
  private Date lastSession;
  @JsonIgnore
  private List<PermissionDto> permissions;

}
