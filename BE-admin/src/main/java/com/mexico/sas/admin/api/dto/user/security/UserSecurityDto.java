package com.mexico.sas.admin.api.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@ToString
public class UserSecurityDto implements Serializable {

  private static final long serialVersionUID = 8174967070638104947L;

  private Long id;
  private Date lastSession;

}
