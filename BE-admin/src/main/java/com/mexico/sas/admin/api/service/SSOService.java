package com.mexico.sas.admin.api.service;

import com.mexico.sas.admin.api.dto.SSORequestDto;
import com.mexico.sas.admin.api.dto.SSOResponseDto;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.dto.SSOUserDto;

public interface SSOService {

  SSOResponseDto login(SSORequestDto ssoRequestDto) throws CustomException;

  boolean validateToken(String token, String validateUrl, String method);

  void logout(String token) throws CustomException;

  SSOUserDto getUserInformation(String token) throws CustomException;

}
