package com.mexico.sas.admin.api.service;

import com.mexico.sas.admin.api.dto.RoleDto;
import com.mexico.sas.admin.api.dto.RoleFindDto;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.model.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoleService {

  void save(RoleDto roleDto) throws CustomException;
  RoleFindDto findById(Long id) throws CustomException;
  Page<RoleFindDto> find(Pageable pageable) throws CustomException;
  Role findEntityById(Long id) throws CustomException;
  Role getOne(Long id);

}
