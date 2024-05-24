package com.mexico.sas.admin.api.dto.sso;

import java.io.Serializable;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SSOUserDto implements Serializable {


  private static final long serialVersionUID = -2651439735952661229L;

  private Long id;
  private String name;
  private String email;
  private String image;
  private Long companyId;
  private String company;
  private String emailDomain;
  private String position;
  private SSORoleDto role;
  private String bossName;
  private String boosEmail;

  public SSOUserDto(Long id) {
    this.id = id;
  }

}
