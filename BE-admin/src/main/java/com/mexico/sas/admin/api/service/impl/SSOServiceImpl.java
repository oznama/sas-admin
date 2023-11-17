package com.mexico.sas.admin.api.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.mexico.sas.admin.api.constants.GeneralKeys;
import com.mexico.sas.admin.api.dto.permission.PermissionDto;
import com.mexico.sas.admin.api.dto.sso.*;
import com.mexico.sas.admin.api.dto.user.UserDto;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.security.CustomJWT;
import io.jsonwebtoken.Claims;
import com.mexico.sas.admin.api.service.SSOService;
import com.mexico.sas.admin.api.service.UserService;
import com.mexico.sas.admin.api.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SSOServiceImpl extends Utils implements SSOService {

  @Autowired
  private CustomJWT customJWT;

  @Autowired
  private UserService userService;

  @Override
  public SSOResponseDto login(SSORequestDto ssoRequestDto) throws CustomException {
    log.debug("Validing user ...");
    return buildSSOResponse(ssoRequestDto);
  }

  /**
   * Validation login and getting session data
   * @param ssoRequestDto
   * @return SSOUserDto
   * @throws CustomException if buildSSOResponse fail
   */
  private SSOResponseDto buildSSOResponse(SSORequestDto ssoRequestDto) throws CustomException {
    SSOResponseDto ssoResponseDto = new SSOResponseDto();
    UserDto userDto = userService.findByEmailAndPassword(ssoRequestDto.getEmail(), ssoRequestDto.getPassword());
    ssoResponseDto.setUser(getSSOUserDto(userDto));
    ssoResponseDto.setAccessToken(customJWT.getToken(ssoRequestDto.getEmail()));
    return ssoResponseDto;
  }

  /**
   * Getting user's permissions
   * @param permissionsDto
   * @return List permissions
   */
  private Collection<SSOPermissionDto> getPermissions(List<PermissionDto> permissionsDto) {
    Collection<SSOPermissionDto> ssoPermissionDtos = new ArrayList<>();
    permissionsDto.forEach( p -> {
      try {
        ssoPermissionDtos.add(from_M_To_N(p, SSOPermissionDto.class));
      } catch (CustomException e) {
        log.warn("Permission with id {} not added to list, error parsing: {}", p.getId(), e.getMessage());
      }
    });
    return ssoPermissionDtos;
  }

  private SSOUserDto getSSOUserDto(UserDto userDto) throws CustomException {
    SSOUserDto ssoUserDto = from_M_To_N(userDto, SSOUserDto.class);
    ssoUserDto.setRole(from_M_To_N(userDto.getRoleDto(), SSORoleDto.class));
    ssoUserDto.getRole().setPermissions(getPermissions(userDto.getPermissions()));
    return ssoUserDto;
  }

  private UserDto getUserFromToken(String token) throws CustomException {
    token = token.startsWith(GeneralKeys.CONSTANT_BEARER) ? token.replace(GeneralKeys.CONSTANT_BEARER, "") : token;
    Claims claims = customJWT.getClaims(token);
    String email = claims.getSubject();
    return userService.findByEmail(email);
  }

}
