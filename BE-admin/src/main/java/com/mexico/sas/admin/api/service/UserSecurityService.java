package com.mexico.sas.admin.api.service;

import com.mexico.sas.admin.api.dto.RetrivePasswordDto;
import com.mexico.sas.admin.api.dto.UserSecurityPasswordDto;
import com.mexico.sas.admin.api.exception.CustomException;

public interface UserSecurityService {

//  UserSecurityDto save(String token, UserSecurityPasswordDto userSecurityPasswordDto) throws CustomException;
//  void update(UserSecurityDto userSecurityDto) throws CustomException;
  void setPassword(String authorization, UserSecurityPasswordDto userSecurityPasswordDto) throws CustomException;
  void recoveryPassword(RetrivePasswordDto retrivePasswordDto, String locale) throws CustomException;

  String validateEmail(String token, String locale) throws CustomException;

}
