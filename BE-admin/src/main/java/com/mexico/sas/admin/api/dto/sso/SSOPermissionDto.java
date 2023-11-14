package com.mexico.sas.admin.api.dto.sso;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SSOPermissionDto implements Serializable {

  private Long id;
  private String name;
  private String description;

}
