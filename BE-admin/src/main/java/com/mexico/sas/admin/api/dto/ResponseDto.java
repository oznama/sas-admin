package com.mexico.sas.admin.api.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ResponseDto implements Serializable {

  private static final long serialVersionUID = -2823940033658218684L;

  private int code;
  private String message;
  private Object entity;

}
