package com.mexico.sas.admin.api.service;

import com.mexico.sas.admin.api.dto.user.*;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

  UserFindDto save(UserDto userDto) throws CustomException;
  UserFindDto update(Long id, UserUpdateDto userUpdateDtos) throws CustomException;
  UserFindDto findById(Long id) throws CustomException;
  User findEntityById(Long id) throws CustomException;
  User getUser(Long id) throws CustomException;
  UserDto findByEmail(String email) throws CustomException;
  UserDto findByEmailAndPassword(String email, String password) throws CustomException;
  Page<UserPaggeableDto> findAll(String filter, Boolean active, Pageable pageable) throws CustomException;
  UserFindDto setActive(Long id, Boolean lock) throws CustomException;
  void deleteLogic(Long id) throws CustomException;

  List<UserSelectFindDto> getForSelect(Long roleId);
}
