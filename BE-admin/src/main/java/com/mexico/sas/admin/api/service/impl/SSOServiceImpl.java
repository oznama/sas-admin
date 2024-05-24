package com.mexico.sas.admin.api.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.mexico.sas.admin.api.dto.company.CompanyFindDto;
import com.mexico.sas.admin.api.dto.permission.PermissionDto;
import com.mexico.sas.admin.api.dto.sso.*;
import com.mexico.sas.admin.api.dto.user.UserDto;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.exception.NoContentException;
import com.mexico.sas.admin.api.model.Employee;
import com.mexico.sas.admin.api.security.CustomJWT;
import com.mexico.sas.admin.api.service.*;
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

  @Autowired
  private EmployeeService employeeService;

  @Autowired
  private CompanyService companyService;

  @Autowired
  private CatalogService catalogService;

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
    Employee employee = employeeService.findEntityByEmail(ssoRequestDto.getEmail());
    UserDto userDto = userService.findByEmployeeAndPassword(employee, ssoRequestDto.getPassword());
    ssoResponseDto.setUser(getSSOUserDto(userDto, employee));
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

  private SSOUserDto getSSOUserDto(UserDto userDto, Employee employee) throws CustomException {
    SSOUserDto ssoUserDto = from_M_To_N(userDto, SSOUserDto.class);
    ssoUserDto.setName(buildFullname(employee));
    ssoUserDto.setEmail(employee.getEmail());
    CompanyFindDto companyFindDto = companyService.findById(employee.getCompanyId());
    ssoUserDto.setCompanyId(companyFindDto.getId());
    ssoUserDto.setCompany(companyFindDto.getName());
    ssoUserDto.setEmailDomain(companyFindDto.getEmailDomain());
    if (Optional.ofNullable(employee.getPositionId()).isPresent())
      ssoUserDto.setPosition(catalogService.findById(employee.getPositionId()).getValue());
    ssoUserDto.setRole(from_M_To_N(userDto.getRoleDto(), SSORoleDto.class));
    ssoUserDto.getRole().setPermissions(getPermissions(userDto.getPermissions()));
    if( employee.getBossId() != null ) {
      try {
        Employee boss = employeeService.findEntityById(employee.getBossId());
        ssoUserDto.setBoosEmail(boss.getEmail());
        ssoUserDto.setBossName(buildFullname(boss));
      } catch (NoContentException e) {
        log.warn("Current user not has boss");
      }
    }
    return ssoUserDto;
  }

}
