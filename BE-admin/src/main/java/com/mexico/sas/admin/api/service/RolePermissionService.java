package com.mexico.sas.admin.api.service;

import com.mexico.sas.admin.api.dto.permission.PermissionFindDto;
import com.mexico.sas.admin.api.dto.role.RolePermissionDto;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.dto.role.RolePermissionsEnaDisDto;
import com.mexico.sas.admin.api.model.RolePermission;

import java.util.List;
import java.util.Map;

public interface RolePermissionService {

  void save(RolePermissionDto rolePermissionDto) throws CustomException;
  List<Map<Long, Boolean>> setActive(List<RolePermissionsEnaDisDto> list);
  void delete(Long id) throws CustomException;

  RolePermission findEntityById(Long id) throws CustomException;

  List<PermissionFindDto> findAllPermissions();
}
