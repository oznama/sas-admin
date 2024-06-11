package com.mexico.sas.admin.api.service;

import com.mexico.sas.admin.api.dto.SelectDto;
import com.mexico.sas.admin.api.dto.role.RoleDto;
import com.mexico.sas.admin.api.dto.role.RoleFindDto;
import com.mexico.sas.admin.api.dto.role.RoleUpdateDto;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.model.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RoleService {

  RoleFindDto save(RoleDto roleDto) throws CustomException;
  RoleUpdateDto update(Long id, RoleUpdateDto roleUpdateDto) throws CustomException;
  RoleFindDto findById(Long id) throws CustomException;
  Page<RoleFindDto> find(Pageable pageable) throws CustomException;
  Role findEntityById(Long id) throws CustomException;
  Role getOne(Long id);
  void deleteLogic(Long id) throws CustomException;
  void delete(Long id) throws CustomException;

  List<SelectDto> getForSelect();

}
