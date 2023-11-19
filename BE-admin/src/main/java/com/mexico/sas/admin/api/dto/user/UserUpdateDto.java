package com.mexico.sas.admin.api.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mexico.sas.admin.api.constants.Regex;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Getter
@Setter
@ToString
public class UserUpdateDto implements Serializable {

  private static final long serialVersionUID = 5343749220582119013L;

  @JsonIgnore
  private Long id;
  private String password;
  @ApiModelProperty(notes = "Roles (1 Administrador", example = "1")
  private Long role;

}
