package com.mexico.sas.admin.api.dto.sso;

import java.io.Serializable;
import java.util.Collection;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SSORoleDto implements Serializable {

  private static final long serialVersionUID = -4803327280406767256L;
  private Long id;
  private String name;
  private String description;
  private Collection<SSOPermissionDto> permissions;
}
