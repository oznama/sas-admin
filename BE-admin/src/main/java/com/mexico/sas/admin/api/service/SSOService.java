package com.mexico.sas.admin.api.service;

import com.mexico.sas.admin.api.dto.sso.SSORequestDto;
import com.mexico.sas.admin.api.dto.sso.SSOResponseDto;
import com.mexico.sas.admin.api.exception.CustomException;

public interface SSOService {

  SSOResponseDto login(SSORequestDto ssoRequestDto) throws CustomException;

}
