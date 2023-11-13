package com.mexico.sas.admin.api.dto;

import java.io.Serializable;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SSORequestDto implements Serializable {

  private static final long serialVersionUID = -7983008838515328478L;

  @NotBlank(message = "{validation.field.required}")
  @Email(message = "{validation.email.format.invalid}")
  private String email;
  @NotBlank(message = "{validation.field.required}")
  private String password;

}
