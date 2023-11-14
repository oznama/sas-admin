package com.mexico.sas.admin.api.dto.user.security;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@ToString
public class UserSecurityPasswordDto implements Serializable {

  @NotBlank(message = "{validation.field.required}")
  private String password;

}
