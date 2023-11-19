package com.mexico.sas.admin.api.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import com.mexico.sas.admin.api.constants.CatalogKeys;
import com.mexico.sas.admin.api.constants.GeneralKeys;
import com.mexico.sas.admin.api.dto.permission.PermissionDto;
import com.mexico.sas.admin.api.dto.role.RoleDto;
import com.mexico.sas.admin.api.dto.user.*;
import com.mexico.sas.admin.api.exception.*;
import com.mexico.sas.admin.api.model.*;
import com.mexico.sas.admin.api.repository.UserRepository;
import com.mexico.sas.admin.api.security.Crypter;
import com.mexico.sas.admin.api.service.*;
import com.mexico.sas.admin.api.i18n.I18nKeys;
import com.mexico.sas.admin.api.i18n.I18nResolver;
import com.mexico.sas.admin.api.util.LogMovementUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@Slf4j
@Service
public class UserServiceImpl extends LogMovementUtils implements UserService {

  @Autowired
  private UserRepository repository;

  @Autowired
  private EmployeeService employeeService;

  @Autowired
  private RoleService roleService;

  @Autowired
  private CatalogService catalogService;

  @Autowired
  private Crypter crypter;

  @Override
  public UserFindDto save(UserDto userDto) throws CustomException {
    log.debug("Saving user {} ...", userDto);
    User user = from_M_To_N(userDto, User.class);
    validationSave(userDto, user);
    try {
      repository.save(user);
      save(User.class.getSimpleName(), user.getId(), CatalogKeys.LOG_DETAIL_INSERT, "TODO");
    } catch (Exception e) {
      String msgError = I18nResolver.getMessage(I18nKeys.USER_NOT_CREATED, userDto.getEmployeeId());
      log.error(msgError, e);
      throw new CustomException(msgError);
    }
    log.debug("User {} created with id {}", userDto.getEmployeeId(), user.getId());
    return findById(user.getId());
  }

  @Override
  public UserFindDto update(Long id, UserUpdateDto userUpdateDto) throws CustomException {
    userUpdateDto.setId(id);
    User user = getUser(userUpdateDto.getId());
    BeanUtils.copyProperties(userUpdateDto, user, "id");
    validationUpdate(userUpdateDto, user);
    repository.save(user);
    save(User.class.getSimpleName(), user.getId(), CatalogKeys.LOG_DETAIL_UPDATE, "TODO");
    log.debug("User {} updated!", user.getId());
    return findById(user.getId());
  }

  @Override
  public UserFindDto findById(Long id) throws CustomException {
    log.debug("Finding user with id: {}", id);
    User user = getUser(id);
    UserFindDto userDto = from_M_To_N(user, UserFindDto.class);
    userDto.setEmployee(employeeService.findById(user.getEmployeeId()));
    userDto.setRole(from_M_To_N(user.getRole(), RoleDto.class));
    userDto.setActions(parsingPermissions(user.getRole().getPermissions()).stream()
            .filter(p -> !p.getName().equals(GeneralKeys.PERMISSION_SPECIAL))
            .collect(Collectors.toList()));
    log.debug("User Finded: {}", userDto.getId());
    return userDto;
  }

  @Override
  public User findEntityById(Long id) throws CustomException {
    return repository.findById(id).orElseThrow(() ->
            new NoContentException(I18nResolver.getMessage(I18nKeys.USER_NOT_FOUND, id)));
  }

  @Override
  public User findEntityByEmployeeId(Long employeeId) throws CustomException {
    return repository.findByEmployeeId(employeeId).orElseThrow(() ->
            new NoContentException(I18nResolver.getMessage(I18nKeys.USER_EMPLOYEE_NOT_FOUND, employeeId)));
  }

  @Override
  public UserDto findByEmployeeAndPassword(Employee employee, String password) throws CustomException {
    UserDto userDto = parse(getUser(employee, crypter.encrypt(password)));
    log.debug("User {} finded", employee.getEmail());
    return userDto;
  }

  @Override
  public Page<UserPaggeableDto> findAll(String filter, Boolean active, Pageable pageable) throws CustomException {
    log.debug("Finding all, active: {}, filter: {}", active, filter);
    final List<UserPaggeableDto> userDtos = new ArrayList<>();
    try {
      Page<User> users = /*StringUtils.isEmpty(filter) ?
              ( active != null ?*/ repository.findByActiveAndEliminateFalse(active, pageable)/* :
                      repository.findByEliminateFalse(pageable)) :
              findByFilter(filter, active, pageable)*/;
      users.forEach( user -> {
        try {
          UserPaggeableDto userDto = from_M_To_N(user, UserPaggeableDto.class);
          userDto.setRole(from_M_To_N(user.getRole(), RoleDto.class));
          userDto.setActions(parsingPermissions(user.getRole().getPermissions()));
          userDtos.add(userDto);
        } catch (Exception e) {
          log.warn("User with id {} not added to list, error parsing: {}", user.getId(), e.getMessage());
        }
      });
      long total = /*StringUtils.isEmpty(filter) ?
              ( active != null ?*/ repository.countByActiveAndEliminateFalse(active)/* :
                      repository.countByEliminateFalse()) :
              countByFilter(filter, active)*/;
      return new PageImpl<>(userDtos, pageable, total);
    } catch (Exception e) {
      throw new BadRequestException(e.getMessage(), pageable.toString());
    }
  }

  @Override
  public UserFindDto setActive(Long id, Boolean lock) throws CustomException {
    Boolean currentActive = repository.findById(id).orElseThrow(() ->
                    new CustomException(I18nResolver.getMessage(I18nKeys.USER_NOT_FOUND)))
            .getActive();
    log.debug("Checking if status equals\nbloqued: {}\nblock? {}", !currentActive, lock);
    if( !currentActive.equals(lock) ) {
      String msgError = I18nResolver.getMessage(lock ? I18nKeys.USER_UPDATE_NOT_ENABLED : I18nKeys.USER_UPDATE_NOT_DISABLED, id);
      throw new CustomException(msgError);
    }
    try {
      repository.setActive(id, !lock);
      save(User.class.getSimpleName(), id, CatalogKeys.LOG_DETAIL_STATUS, "TODO");
      return findById(id);
    } catch (Exception e) {
      throw new CustomException(e.getMessage());
    }
  }

  @Override
  public void deleteLogic(Long id) throws CustomException {
    try {
      getUser(id);
      repository.deleteLogic(id);
      save(User.class.getSimpleName(), id, CatalogKeys.LOG_DETAIL_DELETE_LOGIC, "TODO");
    } catch (Exception e) {
      log.error("Error deleting user", e);
      throw new CustomException(e.getMessage());
    }
  }

  public User getUser(Long id) throws NoContentException {
    return repository.findByIdAndEliminateFalse(id).orElseThrow(() ->
            new NoContentException(I18nResolver.getMessage(I18nKeys.USER_NOT_FOUND, id)));
  }

  private User getUser(Employee employee, String password) throws CustomException {
    log.debug("Finding user {} ...", employee.getEmail());
    return repository.findByEmployeeIdAndPasswordAndEliminateFalse(employee.getId(), password).orElseThrow(() ->
            new LoginException(I18nResolver.getMessage(I18nKeys.LOGIN_USER_NOT_AUTHORIZED)));
  }

  private UserDto parse(User user) throws CustomException {
    UserDto userDto = from_M_To_N(user, UserDto.class);
    userDto.setRole(user.getRole().getId());
    userDto.setRoleDto(from_M_To_N(user.getRole(), RoleDto.class));
    userDto.setPermissions(parsingPermissions(user.getRole().getPermissions()
            .stream().filter(RolePermission::getActive).collect(Collectors.toList())));
    return userDto;
  }

  /**
   * Parsing permissions to permissonsDto
   * @return permissionsDto
   */
  private List<PermissionDto> parsingPermissions(List<RolePermission> permissions) {
    List<PermissionDto> permissionsDto = new ArrayList<>();
    permissions.forEach( p -> {
      try {
        PermissionDto permissionDto = from_M_To_N(p.getPermission(), PermissionDto.class);
        BeanUtils.copyProperties(p, permissionDto);
        permissionsDto.add(permissionDto);
      } catch (CustomException e) {
        log.warn("Permission with id {} not added to list, error parsing: {}", p.getId(), e.getMessage());
      }
    });
    return permissionsDto;
  }


  private void validationSave(UserDto userDto, User user) throws CustomException {
    // Validacion de rol
    user.setRole(roleService.findEntityById(userDto.getRole()));
    user.setCreatedBy(getCurrentUserId());
  }

  private void validationUpdate(UserUpdateDto userDto, User user) throws CustomException {

    // Validacion de rol
    user.setRole(roleService.findEntityById(userDto.getRole()));
  }

//  private Page<User> findByFilter(String filter, Boolean active, Pageable pageable) {
//    return repository.findAll((Specification<User>) (root, query, criteriaBuilder) -> getPredicateDinamycFilter(filter, active, criteriaBuilder, root), pageable);
//  }
//
//  private Long countByFilter(String filter, Boolean active) {
//    return repository.count((Specification<User>) (root, query, criteriaBuilder) -> getPredicateDinamycFilter(filter, active, criteriaBuilder, root));
//  }

//  private Predicate getPredicateDinamycFilter(String filter, Boolean active, CriteriaBuilder builder, Root<User> root) {
//
//    Predicate pEliminated = builder.isFalse(root.get(User.Fields.eliminate));
//
//    String patternFilter = String.format(GeneralKeys.PATTERN_LIKE, filter.toLowerCase());
//    Predicate pName = builder.like(builder.lower(root.get(User.Fields.name)), patternFilter);
//    Predicate pSurname = builder.like(builder.lower(root.get(User.Fields.surname)), patternFilter);
//    Predicate pSecondSurname = builder.like(builder.lower(root.get(User.Fields.secondSurname)), patternFilter);
//    Predicate pEmail = builder.like(builder.lower(root.get(User.Fields.email)), patternFilter);
//    Predicate pPhone = builder.like(builder.lower(root.get(User.Fields.phone)), patternFilter);
//    Predicate pNameA = builder.like(
//            builder.function(GeneralKeys.PG_FUNCTION_ACCENT,String.class, builder.lower(root.get(User.Fields.name))), patternFilter);
//    Predicate pSurnameA = builder.like(
//            builder.function(GeneralKeys.PG_FUNCTION_ACCENT,String.class, builder.lower(root.get(User.Fields.surname))), patternFilter);
//    Predicate pSecondSurnameA = builder.like(
//            builder.function(GeneralKeys.PG_FUNCTION_ACCENT,String.class, builder.lower(root.get(User.Fields.secondSurname))), patternFilter);
//
//    Predicate pFilter = builder.or(pName, pSurname, pSecondSurname, pNameA, pSurnameA, pSecondSurnameA, pEmail, pPhone);
//    if(active == null)
//      return builder.and(pEliminated, pFilter);
//    else {
//      Predicate pActive = builder.equal(root.get(User.Fields.active), active);
//      return builder.and(pEliminated, pActive, pFilter);
//    }
//  }

}
