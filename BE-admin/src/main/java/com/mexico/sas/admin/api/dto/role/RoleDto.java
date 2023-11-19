package com.mexico.sas.admin.api.dto.role;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
public class RoleDto implements Serializable {

  private static final long serialVersionUID = 6493881991195092562L;

  @NotNull(message = "{validation.field.required}")
  private Long id;
  private String name;
  private String description;

}
