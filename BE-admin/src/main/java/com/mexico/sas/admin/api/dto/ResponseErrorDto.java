package com.mexico.sas.admin.api.dto;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ResponseErrorDto implements Serializable {

  private static final long serialVersionUID = -2823940033658218684L;

  private int code;
  private List<ResponseErrorDetailDto> errors;

}
