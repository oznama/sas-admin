package com.mexico.sas.admin.api.service.impl;

import com.mexico.sas.admin.api.constants.CatalogKeys;
import com.mexico.sas.admin.api.dto.role.RolePermissionDto;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.exception.NoContentException;
import com.mexico.sas.admin.api.repository.RolePermisionRepository;
import com.mexico.sas.admin.api.dto.role.RolePermissionsEnaDisDto;
import com.mexico.sas.admin.api.i18n.I18nKeys;
import com.mexico.sas.admin.api.i18n.I18nResolver;
import com.mexico.sas.admin.api.model.RolePermission;
import com.mexico.sas.admin.api.service.RolePermissionService;
import com.mexico.sas.admin.api.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class RolePermissionServiceImpl extends Utils implements RolePermissionService {

  @Autowired
  private RolePermisionRepository repository;

  @Override
  public void save(RolePermissionDto rolePermissionDto) throws CustomException {
    log.debug("Saving role-permission relation {} ...", rolePermissionDto);
    RolePermission rolePermission = from_M_To_N(rolePermissionDto, RolePermission.class);
    rolePermission.setCreationDate(new Date());
    rolePermission.setUserId(getCurrentUserId());
    repository.save(rolePermission);
    if(rolePermission.getId() == null) {
      String msgError = I18nResolver.getMessage(I18nKeys.ROLE_PERMISSION_NOT_CREATED, rolePermissionDto);
      log.error(msgError);
      throw new CustomException(msgError);
    }
    rolePermissionDto.setId(rolePermission.getId());
    save(RolePermission.class.getSimpleName(), rolePermission.getId(), null, rolePermission.getUserId(),
            rolePermission.getCreationDate(), CatalogKeys.LOG_EVENT_INSERT, CatalogKeys.LOG_DETAIL_INSERT);
    log.debug("Relation role-permission created with id {}", rolePermissionDto.getId());
  }

  @Override
  public List<Map<Long, Boolean>> setActive(List<RolePermissionsEnaDisDto> list){
    final List<Map<Long, Boolean>> results = new ArrayList<>();
    Date currentDate = new Date();
      list.forEach( rp -> {
        try {
        repository.setActive(rp.getId(), rp.getActive());
        final Map<Long, Boolean> result = new HashMap<>();
        result.put(rp.getId(), Boolean.TRUE);
        save(RolePermission.class.getSimpleName(), rp.getId(), null, getCurrentUserId(),
                currentDate, CatalogKeys.LOG_EVENT_UPDATE, CatalogKeys.LOG_DETAIL_STATUS);
        results.add(result);
        } catch (Exception e) {
          log.warn("Permission {} not {}, error: {}", rp.getId(), rp.getActive() ? "activated" : "inactivated", e.getMessage());
          final Map<Long, Boolean> result = new HashMap<>();
          result.put(rp.getId(), Boolean.FALSE);
          results.add(result);
        }
      } );
      return results;
  }

  @Override
  public void delete(Long id) throws CustomException {
    RolePermission rolePermission = findEntityById(id);
    try {
      repository.delete(rolePermission);
      save(RolePermission.class.getSimpleName(), rolePermission.getId(), null, getCurrentUserId(),
              new Date(), CatalogKeys.LOG_EVENT_DELETE, CatalogKeys.LOG_DETAIL_DELETE);
    } catch (Exception e) {
      throw new CustomException(e.getMessage());
    }
  }

  @Override
  public RolePermission findEntityById(Long id) throws CustomException {
    return repository.findById(id).orElseThrow(() ->
            new NoContentException(I18nResolver.getMessage(I18nKeys.PERMISSION_NOT_FOUND)));
  }

}
