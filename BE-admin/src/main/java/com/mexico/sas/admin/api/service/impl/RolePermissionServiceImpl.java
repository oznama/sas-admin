package com.mexico.sas.admin.api.service.impl;

import com.mexico.sas.admin.api.constants.CatalogKeys;
import com.mexico.sas.admin.api.dto.SelectDto;
import com.mexico.sas.admin.api.dto.employee.EmployeeDto;
import com.mexico.sas.admin.api.dto.employee.EmployeeFindDto;
import com.mexico.sas.admin.api.dto.permission.PermissionFindDto;
import com.mexico.sas.admin.api.dto.role.*;
import com.mexico.sas.admin.api.exception.BadRequestException;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.exception.NoContentException;
import com.mexico.sas.admin.api.model.*;
import com.mexico.sas.admin.api.repository.PermissionRepository;
import com.mexico.sas.admin.api.repository.RolePermisionRepository;
import com.mexico.sas.admin.api.i18n.I18nKeys;
import com.mexico.sas.admin.api.i18n.I18nResolver;
import com.mexico.sas.admin.api.repository.RoleRepository;
import com.mexico.sas.admin.api.service.RolePermissionService;
import com.mexico.sas.admin.api.util.LogMovementUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.support.Repositories;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class RolePermissionServiceImpl extends LogMovementUtils implements RolePermissionService {
  @Autowired
  private RoleRepository roleRepository;
  /*@Override
  public List<Map<Long, Boolean>> setActive(List<RolePermissionsEnaDisDto> list){
    final List<Map<Long, Boolean>> results = new ArrayList<>();
    Date currentDate = new Date();
      list.forEach( rp -> {
        try {
        repository.setActive(rp.getId(), rp.getActive());
        final Map<Long, Boolean> result = new HashMap<>();
        result.put(rp.getId(), Boolean.TRUE);
        save(RolePermission.class.getSimpleName(), rp.getId(), CatalogKeys.LOG_DETAIL_STATUS, "TODO");
        results.add(result);
        } catch (Exception e) {
          log.warn("Permission {} not {}, error: {}", rp.getId(), rp.getActive() ? "activated" : "inactivated", e.getMessage());
          final Map<Long, Boolean> result = new HashMap<>();
          result.put(rp.getId(), Boolean.FALSE);
          results.add(result);
        }
      } );
      return results;
  }*/

  /*@Override
  public void delete(Long id) throws CustomException {
    RolePermission rolePermission = findEntityById(id);
    try {
      repository.delete(rolePermission);
      save(RolePermission.class.getSimpleName(), rolePermission.getId(), CatalogKeys.LOG_DETAIL_DELETE, "TODO");
    } catch (Exception e) {
      throw new CustomException(e.getMessage());
    }
  }*/
  @Autowired
  private RolePermisionRepository repository;

  @Autowired
  private PermissionRepository permissionRepository;

//  @Override
//  public void save(RolePermissionDto rolePermissionDto) throws CustomException {
//    log.debug("Saving role-permission relation {} ...", rolePermissionDto);
//    RolePermission rolePermission = from_M_To_N(rolePermissionDto, RolePermission.class);
//    rolePermission.setCreatedBy(getCurrentUser().getUserId());
//    repository.save(rolePermission);
//    if(rolePermission.getId() == null) {
//      String msgError = I18nResolver.getMessage(I18nKeys.ROLE_PERMISSION_NOT_CREATED, rolePermissionDto);
//      log.error(msgError);
//      throw new CustomException(msgError);
//    }
//    rolePermissionDto.setId(rolePermission.getId());
//    save(RolePermission.class.getSimpleName(), rolePermission.getId(), CatalogKeys.LOG_DETAIL_INSERT, "TODO");
//    log.debug("Relation role-permission created with id {}", rolePermissionDto.getId());
//  }
  @Override
  public RolePermissionsFindDto save(RolePermissionDto rolePermissionDto) throws CustomException {
    RolePermission rolePermission = from_M_To_N(rolePermissionDto, RolePermission.class);
    validationSave(rolePermissionDto, rolePermission);
//    rolePermission.setPermission(permissionRepository.findById(rolePermissionDto.getPermissionId()).get());
//    rolePermission.setRole(roleRepository.findById(rolePermissionDto.getRoleId()).get());
    repository.save(rolePermission);
    RolePermissionsFindDto rolePermissionsFindDto = from_M_To_N(rolePermissionDto, RolePermissionsFindDto.class);
    rolePermissionsFindDto.setId(rolePermission.getId());
    save(Employee.class.getSimpleName(), rolePermission.getId(), CatalogKeys.LOG_DETAIL_INSERT,
            I18nResolver.getMessage(I18nKeys.LOG_GENERAL_CREATION));
    return rolePermissionsFindDto;
  }
  @Override
  public RolePermission findEntityById(Long id) throws CustomException {
    return repository.findById(id).orElseThrow(() ->
            new NoContentException(I18nResolver.getMessage(I18nKeys.PERMISSION_NOT_FOUND)));
  }

  @Override
  public List<PermissionFindDto> findAllPermissions() {
    List<Permission> permissions = permissionRepository.findAll();
    List<PermissionFindDto> permissionFindDtos = new ArrayList<>();
    permissions.forEach( permission -> {
      try {
        PermissionFindDto permissionFindDto = from_M_To_N(permission, PermissionFindDto.class);
        // If is necessary map custom propertys do it here
        permissionFindDtos.add(permissionFindDto);
      } catch (CustomException e) {
        log.warn("Error to parse permission {}, error: {}", permission.getId(), e.getMessage());
      }

    });
    return permissionFindDtos;
  }

  @Override
  public List<PermissionFindDto> findByRoleId(Long roleId) {
    List<RolePermission> rolePermissions = findEntityByRole(new Role(roleId));
    List<PermissionFindDto> permissionFindDtos = new ArrayList<>();
    rolePermissions.forEach( rolePermission -> {
      try {
        PermissionFindDto permissionFindDto = from_M_To_N(rolePermission.getPermission(), PermissionFindDto.class);
        permissionFindDtos.add(permissionFindDto);
      } catch (CustomException e) {
        log.warn("Error to parse permission {}, error: {}", rolePermission.getId(), e.getMessage());
      }

    });
    return permissionFindDtos;
  }

  @Override
  public List<RolePermission> findEntityByRole(Role role) {
    return repository.findByRole(role);
  }

  @Override
  public void deleteLogic(Long id) throws CustomException {
    log.debug("Delete logic: {}", id);
    RolePermission rolePermission = findEntityById(id);
    repository.deleteLogic(id, !rolePermission.getEliminate(), rolePermission.getEliminate());
    save(Project.class.getSimpleName(), id,
            !rolePermission.getEliminate() ? CatalogKeys.LOG_DETAIL_DELETE_LOGIC : CatalogKeys.LOG_DETAIL_STATUS,
            I18nResolver.getMessage(!rolePermission.getEliminate() ? I18nKeys.LOG_GENERAL_DELETE : I18nKeys.LOG_GENERAL_REACTIVE));
  }

  @Override
  public void delete(Long id) throws CustomException {
    try{
      repository.deleteById(id);
      save(RolePermission.class.getSimpleName(), id, CatalogKeys.LOG_DETAIL_DELETE, "TODO");
    } catch (Exception e) {
      //throw new CustomException(I18nResolver.getMessage(I18nKeys.CATALOG_NOT_DELETED, id));
      throw new CustomException(e.getMessage());
    }
  }

  private void validationSave(RolePermissionDto rolePermissionDto, RolePermission rolePermission) throws CustomException {
//    try {
//      findEntityById(rolePermissionDto.getId());
//      throw new BadRequestException(I18nResolver.getMessage(I18nKeys.VALIDATION_EMAIL_DUPLICATED, rolePermissionDto.getId()), null);
//    } catch (CustomException e) {
//      if(e instanceof BadRequestException)
//        throw e;
//    }
    rolePermission.setRole(roleRepository.findById(rolePermissionDto.getRoleId()).get());
    rolePermission.setPermission(permissionRepository.findById(rolePermissionDto.getPermissionId()).get());
  }

}
