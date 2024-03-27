package com.mexico.sas.admin.api.service.impl;

import com.mexico.sas.admin.api.constants.CatalogKeys;
import com.mexico.sas.admin.api.dto.permission.PermissionFindDto;
import com.mexico.sas.admin.api.dto.role.RoleDto;
import com.mexico.sas.admin.api.dto.role.RoleFindDto;
import com.mexico.sas.admin.api.dto.role.RoleUpdateDto;
import com.mexico.sas.admin.api.exception.BadRequestException;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.exception.NoContentException;
import com.mexico.sas.admin.api.model.*;
import com.mexico.sas.admin.api.repository.RoleRepository;
import com.mexico.sas.admin.api.i18n.I18nKeys;
import com.mexico.sas.admin.api.i18n.I18nResolver;
import com.mexico.sas.admin.api.service.RoleService;
import com.mexico.sas.admin.api.service.UserService;
import com.mexico.sas.admin.api.util.ChangeBeanUtils;
import com.mexico.sas.admin.api.util.LogMovementUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class RoleServiceImpl extends LogMovementUtils implements RoleService {

  @Autowired
  private RoleRepository repository;

  @Autowired
  private UserService userService;

  @Override
  public RoleFindDto save(RoleDto roleDto) throws CustomException {
    log.debug("Saving role {} ...", roleDto);
    Role role = from_M_To_N(roleDto, Role.class);
    role.setCreatedBy(getCurrentUser().getUserId());
    repository.save(role);
    if(role.getId() == null) {
      String msgError = I18nResolver.getMessage(I18nKeys.ROLE_NOT_CREATED, role.getName());
      log.error(msgError);
      throw new CustomException(msgError);
    }
    roleDto.setId(role.getId());
    save(Role.class.getSimpleName(), role.getId(), CatalogKeys.LOG_DETAIL_INSERT, "TODO");
    log.debug("Role created with id {}", roleDto.getId());
    return ParseDtoToFindDto(roleDto);
  }

  public RoleFindDto ParseDtoToFindDto(RoleDto roleDto){
    RoleFindDto roleFindDto = new RoleFindDto();
    roleFindDto.setId(roleDto.getId());
    roleFindDto.setName(roleDto.getName());
    roleFindDto.setDescription(roleDto.getDescription());
    roleFindDto.setActive(roleDto.getActive());
    roleFindDto.setEliminate(roleDto.getEliminate());
    return roleFindDto;
  }
  @Override
  public RoleUpdateDto update(Long id, RoleUpdateDto roleDto) throws CustomException {
    Role role = findEntityById(id);
    roleDto.setId(id);
    String message = ChangeBeanUtils.checkRole(role, roleDto, this);
    if (!message.isEmpty()){
      repository.save(role);
      save(Role.class.getSimpleName(), role.getId(), CatalogKeys.LOG_DETAIL_UPDATE, message);
    }
    return roleDto;
  }

  @Override
  public RoleFindDto findById(Long id) throws CustomException {
    Role role = findEntityById(id);
    RoleFindDto roleDto = from_M_To_N(role, RoleFindDto.class);
    roleDto.setPermissions(getPermissions(role.getPermissions()));
    return roleDto;
  }

  @Override
  public Page<RoleFindDto> find(Pageable pageable) throws CustomException {
    final List<RoleFindDto>  roleDtos = new ArrayList<>();
    try {
      Page<Role> roles = repository.findAll(pageable);
      roles.forEach( role -> {
        try {
          RoleFindDto roleDto = from_M_To_N(role, RoleFindDto.class);
          roleDto.setPermissions(getPermissions(role.getPermissions()));
          roleDtos.add(roleDto);
        } catch (Exception e) {
          log.warn("Role with id {} not added to list, error parsing: {}", role.getId(), e.getMessage());
        }
      } );
      return new PageImpl<>(roleDtos, pageable, repository.count());
    } catch (Exception e) {
      throw new BadRequestException(e.getMessage(), pageable.toString());
    }
  }


  @Override
  public Role findEntityById(Long id) throws CustomException {
    log.debug("Find role by id: {}", id);
    final String msgError = I18nResolver.getMessage(I18nKeys.ROLE_NOT_FOUND, id);
    return repository.findById(id)
            .orElseThrow(() -> new NoContentException(msgError) );
  }

  @Override
  public Role getOne(Long id) {
    return repository.getOne(id);
  }

  @Override
  public void deleteLogic(Long id) throws CustomException{
    log.debug("Delete logic: {}", id);
    Role role = findEntityById(id);
    repository.deleteLogic(id, !role.getEliminate(), role.getEliminate());
    save(Project.class.getSimpleName(), id,
            !role.getEliminate() ? CatalogKeys.LOG_DETAIL_DELETE_LOGIC : CatalogKeys.LOG_DETAIL_STATUS,
            I18nResolver.getMessage(!role.getEliminate() ? I18nKeys.LOG_GENERAL_DELETE : I18nKeys.LOG_GENERAL_REACTIVE));
  }

  @Override
  public void delete(Long id) throws CustomException {
    findEntityById(id);
    try{
      repository.deleteById(id);
      save(Role.class.getSimpleName(), id, CatalogKeys.LOG_DETAIL_DELETE, "TODO");
    } catch (Exception e) {
      throw new CustomException(I18nResolver.getMessage(I18nKeys.CATALOG_NOT_DELETED, id));
    }
  }

  private Collection<PermissionFindDto> getPermissions(List<RolePermission> permissionsDto) {
    Collection<PermissionFindDto> permissionFindDtos = new ArrayList<>();
    permissionsDto.forEach( p -> {
      try {
        PermissionFindDto permissionFindDto = from_M_To_N(p, PermissionFindDto.class);
        permissionFindDto.setName(p.getPermission().getName());
        permissionFindDto.setDescription(p.getPermission().getDescription());
//        permissionFindDtos.add(permissionFindDto);
      } catch (CustomException e) {
        log.warn("Permission with id {} not added to list error parsing: {}", p.getId(), e.getMessage());
      }
    });
    return permissionFindDtos;
  }

}
