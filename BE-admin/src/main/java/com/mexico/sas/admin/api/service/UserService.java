package com.mexico.sas.admin.api.service;

import com.mexico.sas.admin.api.dto.user.*;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.model.Employee;
import com.mexico.sas.admin.api.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

  UserFindDto save(UserDto userDto) throws CustomException;
  void update(Long id, UserUpdateDto userUpdateDtos) throws CustomException;
  UserFindDto findById(Long id) throws CustomException;
  User findEntityById(Long id) throws CustomException;
  User findEntityByEmployeeId(Long employeeId) throws CustomException;
  User getUser(Long id) throws CustomException;
  UserDto findByEmployeeAndPassword(Employee employee, String password) throws CustomException;
  Page<UserPaggeableDto> findAll(String filter, Boolean active, Pageable pageable) throws CustomException;
  List<UserIdsDto> getUsersIds() throws CustomException;
  void deleteLogic(Long id) throws CustomException;

  void resetPswd(Long id) throws CustomException;
}
