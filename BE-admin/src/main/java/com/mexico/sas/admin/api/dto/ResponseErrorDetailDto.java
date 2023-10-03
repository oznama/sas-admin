package com.mexico.sas.admin.api.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ResponseErrorDetailDto implements Serializable {

  private static final long serialVersionUID = -2823940033658218684L;

  private String entity;
  private String message;

}
