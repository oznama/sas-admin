package com.mexico.sas.admin.api.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Getter
@Setter
@ToString
public class UserUpdateDto implements Serializable {

  private static final long serialVersionUID = 5343749220582119013L;

  @JsonIgnore
  private Long id;
  @NotBlank(message = "{validation.field.required}")
  private String currentPassword;
  @NotBlank(message = "{validation.field.required}")
  private String newPassword;
  @ApiModelProperty(notes = "Roles (1 Administrador", example = "1")
  private Long role;

}
