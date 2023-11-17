package com.mexico.sas.admin.api.service;

import com.mexico.sas.admin.api.dto.user.security.UserSecurityPasswordDto;
import com.mexico.sas.admin.api.exception.CustomException;

public interface UserSecurityService {
  void setPassword(Long id, UserSecurityPasswordDto userSecurityPasswordDto) throws CustomException;

}
