package com.mexico.sas.admin.api.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
public class UserSelectFindDto implements Serializable {

  private static final long serialVersionUID = 973579655714936807L;

  private Long id;
  private String value;

}
