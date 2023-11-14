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

  @NotBlank(message = "{validation.name.required}")
  @Pattern(regexp = Regex.ONLY_LETTERS_WHIT_SPACE, message = "{validation.format.invalid} {validation.only.letter}")
  private String name;

  @NotBlank(message = "{validation.lastname.requred}")
  @Pattern(regexp = Regex.ONLY_LETTERS_WHIT_SPACE, message = "{validation.format.invalid} {validation.only.letter}")
  private String surname;

  @Pattern(regexp = Regex.ONLY_LETTERS_WHIT_SPACE, message = "{validation.format.invalid} {validation.only.letter}")
  private String secondSurname;

  @NotBlank(message = "{validation.phone.required}")
  @Pattern(regexp = Regex.ONLY_NUMBERS, message = "{validation.format.invalid} {validation.only.number}")
  private String phone;
  @ApiModelProperty(notes = "Roles (1 Administrador", example = "1")
  private Long role;
  private String image;

}
