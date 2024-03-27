package com.mexico.sas.admin.api.service;

import com.mexico.sas.admin.api.dto.SelectDto;
import com.mexico.sas.admin.api.dto.permission.PermissionFindDto;
import com.mexico.sas.admin.api.dto.role.*;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.model.Role;
import com.mexico.sas.admin.api.model.RolePermission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface RolePermissionService {
  /*List<Map<Long, Boolean>> setActive(List<RolePermissionsEnaDisDto> list);*/
  RolePermissionsFindDto save(RolePermissionDto rolePermissionDto) throws CustomException;
  RolePermission findEntityById(Long id) throws CustomException;
  List<PermissionFindDto> findAllPermissions();
  List<PermissionFindDto> findByRoleId(Long roleId);

  List<RolePermission> findEntityByRole(Role role);
  void deleteLogic(Long id) throws CustomException;
  void delete(Long id) throws CustomException;
}
