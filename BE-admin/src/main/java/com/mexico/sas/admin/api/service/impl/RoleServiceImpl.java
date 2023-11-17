package com.mexico.sas.admin.api.service.impl;

import com.mexico.sas.admin.api.constants.CatalogKeys;
import com.mexico.sas.admin.api.dto.permission.PermissionFindDto;
import com.mexico.sas.admin.api.dto.role.RoleDto;
import com.mexico.sas.admin.api.dto.role.RoleFindDto;
import com.mexico.sas.admin.api.exception.BadRequestException;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.exception.NoContentException;
import com.mexico.sas.admin.api.repository.RoleRepository;
import com.mexico.sas.admin.api.service.CatalogService;
import com.mexico.sas.admin.api.i18n.I18nKeys;
import com.mexico.sas.admin.api.i18n.I18nResolver;
import com.mexico.sas.admin.api.model.Role;
import com.mexico.sas.admin.api.model.RolePermission;
import com.mexico.sas.admin.api.service.RoleService;
import com.mexico.sas.admin.api.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class RoleServiceImpl extends Utils implements RoleService {

  @Autowired
  private RoleRepository repository;

  @Autowired
  private CatalogService catalogService;

  @Override
  public void save(RoleDto roleDto) throws CustomException {
    log.debug("Saving role {} ...", roleDto);
    Role role = from_M_To_N(roleDto, Role.class);
    role.setCreationDate(new Date());
    role.setUserId(getCurrentUserId());
    repository.save(role);
    if(role.getId() == null) {
      String msgError = I18nResolver.getMessage(I18nKeys.ROLE_NOT_CREATED, role.getName());
      log.error(msgError);
      throw new CustomException(msgError);
    }
    roleDto.setId(role.getId());
    save(Role.class.getSimpleName(), role.getId(), role.getUserId(), role.getCreationDate(),
            CatalogKeys.LOG_EVENT_INSERT, CatalogKeys.LOG_DETAIL_INSERT);
    log.debug("Role created with id {}", roleDto.getId());
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

  private Collection<PermissionFindDto> getPermissions(List<RolePermission> permissionsDto) {
    Collection<PermissionFindDto> permissionFindDtos = new ArrayList<>();
    permissionsDto.forEach( p -> {
      try {
        PermissionFindDto permissionFindDto = from_M_To_N(p, PermissionFindDto.class);
        permissionFindDto.setName(p.getPermission().getName());
        permissionFindDto.setDescription(p.getPermission().getDescription());
        permissionFindDtos.add(permissionFindDto);
      } catch (CustomException e) {
        log.warn("Permission with id {} not added to list error parsing: {}", p.getId(), e.getMessage());
      }
    });
    return permissionFindDtos;
  }

}
