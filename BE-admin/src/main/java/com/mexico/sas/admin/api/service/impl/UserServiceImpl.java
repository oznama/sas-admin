package com.mexico.sas.admin.api.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import com.mexico.sas.admin.api.constants.CatalogKeys;
import com.mexico.sas.admin.api.constants.GeneralKeys;
import com.mexico.sas.admin.api.constants.TemplateKeys;
import com.mexico.sas.admin.api.dto.employee.EmployeeDto;
import com.mexico.sas.admin.api.dto.employee.EmployeeFindDto;
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
import com.mexico.sas.admin.api.util.EmailUtils;
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
  private RolePermissionService rolePermissionService;

  @Autowired
  private Crypter crypter;

  @Autowired
  private EmailUtils emailUtils;

  @Override
  public UserFindDto save(UserDto userDto) throws CustomException {
    log.debug("Saving user {} ...", userDto);
    User user = from_M_To_N(userDto, User.class);
    validationSave(userDto, user);
    try {
      repository.save(user);
      save(User.class.getSimpleName(), user.getId(), CatalogKeys.LOG_DETAIL_INSERT, I18nResolver.getMessage(I18nKeys.LOG_GENERAL_CREATION));
    } catch (Exception e) {
      String msgError = I18nResolver.getMessage(I18nKeys.USER_NOT_CREATED, userDto.getEmployeeId());
      log.error(msgError, e);
      throw new CustomException(msgError);
    }
    log.debug("User for employe {} created with id {} and pswd: {}", userDto.getEmployeeId(), user.getId(), userDto.getPassword());

    Map<String, Object> variables = new HashMap<>();
    variables.put("employeeName", buildFullname(user.getEmployee()));
    variables.put("userName", user.getEmployee().getEmail());
    variables.put("passwordTmp", userDto.getPassword());
    emailUtils.sendMessage(user.getEmployee().getEmail(), GeneralKeys.EMAIL_SUBJECT_USER_CREATED, TemplateKeys.USER_CREATED, variables);

    return findById(user.getId());
  }

  @Override
  public void update(Long id, UserUpdateDto userUpdateDto) throws CustomException {
    userUpdateDto.setId(id);
    User user = getUser(userUpdateDto.getId());
    BeanUtils.copyProperties(userUpdateDto, user, "id");
    String message = validationUpdate(userUpdateDto, user);
    if(!message.isEmpty()) {
      repository.save(user);
      save(User.class.getSimpleName(), user.getId(), CatalogKeys.LOG_DETAIL_UPDATE, message);
      log.debug("User {} updated!", user.getId());
    }
  }

  @Override
  public UserFindDto findById(Long id) throws CustomException {
    log.debug("Finding user with id: {}", id);
    User user = getUser(id);
    UserFindDto userDto = from_M_To_N(user, UserFindDto.class);
    userDto.setEmployee(from_M_To_N(user.getEmployee(), EmployeeFindDto.class));
    userDto.setRole(from_M_To_N(user.getRole(), RoleDto.class));
    log.debug("User Finded: {}", userDto.getId());
    return userDto;
  }

  @Override
  public User findEntityById(Long id) throws CustomException {
    return repository.findById(id).orElseThrow(() ->
            new NoContentException(I18nResolver.getMessage(I18nKeys.USER_NOT_FOUND, id)));
  }

  @Override
  public User findEntityByEmployee(Employee employee) throws CustomException {
    return repository.findByEmployee(employee).orElseThrow(() ->
            new NoContentException(I18nResolver.getMessage(I18nKeys.USER_EMPLOYEE_NOT_FOUND, employee.getId())));
  }

  @Override
  public UserDto findByEmployeeAndPassword(Employee employee, String password) throws CustomException {
    UserDto userDto = parse(getUser(employee, password));
    log.debug("User {} finded", employee.getEmail());
    return userDto;
  }

  @Override
  public Page<UserPaggeableDto> findAll(String filter, Pageable pageable) throws CustomException {
    log.debug("Finding all, filter: {}", filter);
    final List<UserPaggeableDto> userDtos = new ArrayList<>();
    try {
      Page<User> users = findByFilter(filter, null, pageable);
      users.forEach( user -> {
        try {
          UserPaggeableDto userDto = from_M_To_N(user, UserPaggeableDto.class);
          userDto.setEmployee(from_M_To_N(user.getEmployee(), EmployeeDto.class));
          userDto.setRole(from_M_To_N(user.getRole(), RoleDto.class));
          userDtos.add(userDto);
        } catch (Exception e) {
          log.warn("User with id {} not added to list, error parsing: {}", user.getId(), e.getMessage());
        }
      });
      return new PageImpl<>(userDtos, pageable, users.getTotalElements());
    } catch (Exception e) {
      throw new BadRequestException(e.getMessage(), pageable.toString());
    }
  }

  @Override
  public List<UserIdsDto> getUsersIds() throws CustomException {
    List<User> users = repository.findAll();
    List<UserIdsDto> userIdsDtos = new ArrayList<>();
    users.forEach( u -> userIdsDtos.add(new UserIdsDto(u.getId(), u.getEmployee().getId(), u.getRole().getId())));
    return userIdsDtos;
  }

  @Override
  public void deleteLogic(Long id) throws CustomException {
    log.debug("Delete logic: {}", id);
    User user = getUser(id);
    repository.deleteLogic(id, !user.getEliminate(), user.getEliminate());
    save(User.class.getSimpleName(), id,
            !user.getEliminate() ? CatalogKeys.LOG_DETAIL_DELETE_LOGIC : CatalogKeys.LOG_DETAIL_STATUS,
            I18nResolver.getMessage(!user.getEliminate() ? I18nKeys.LOG_GENERAL_DELETE : I18nKeys.LOG_GENERAL_REACTIVE));
  }

  @Override
  public void resetPswd(Long id) throws CustomException {
    User user = getUser(id);
    String randomPasword = generateRandomPswd();
    user.setPassword(crypter.encrypt(randomPasword));
    repository.save(user);
    save(User.class.getSimpleName(), user.getId(), CatalogKeys.LOG_DETAIL_UPDATE, I18nResolver.getMessage(I18nKeys.USER_SECURITY_PSWD_RESET));
    log.debug("User {}'s password reset for: {}", user.getId(), randomPasword);
    Map<String, Object> variables = new HashMap<>();
    variables.put("employeeName", buildFullname(user.getEmployee()));
    variables.put("passwordTmp", randomPasword);
    emailUtils.sendMessage(user.getEmployee().getEmail(), GeneralKeys.EMAIL_SUBJECT_PSWD_RESET, TemplateKeys.PSWD_RESET, variables);
  }

  public User getUser(Long id) throws NoContentException {
    return repository.findById(id).orElseThrow(() ->
            new NoContentException(I18nResolver.getMessage(I18nKeys.USER_NOT_FOUND, id)));
  }

  private User getUser(Employee employee, String password) throws CustomException {
    log.debug("Finding user {} ...", employee.getEmail());
    User user = repository.findByEmployeeAndPassword(employee, password).orElseThrow(() ->
            new LoginException(I18nResolver.getMessage(I18nKeys.LOGIN_USER_NOT_AUTHORIZED)));

    user.getRole().setPermissions( rolePermissionService.findEntityByRole(user.getRole()) );
    return user;
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
    // Validacion empleado
    Employee employee = employeeService.findEntityById(userDto.getEmployeeId());
    boolean isValid = false;
    try {
      findEntityByEmployee(employee);
    } catch (CustomException e) {
      if(e instanceof NoContentException) {
        isValid = true;
      }
    }
    if(isValid) {
      // Validacion de rol
      user.setRole(roleService.findEntityById(userDto.getRole()));
      user.setEmployee(employee);
      user.setCreatedBy(getCurrentUser().getUserId());
      String randomPasword = generateRandomPswd();
      userDto.setPassword(randomPasword);
      user.setPassword(crypter.encrypt(randomPasword));
    } else {
      throw new BadRequestException(I18nResolver.getMessage(I18nKeys.EMPLOYEE_USER_EXIST, buildFullname(employee)), null);
    }
  }

  private String validationUpdate(UserUpdateDto userDto, User user) throws CustomException {
    StringBuilder sb = new StringBuilder();
    String passwordDecrypted = crypter.decrypt(user.getPassword());
    // Validation currentPassword
    if(!StringUtils.isEmpty(userDto.getCurrentPassword()) && !userDto.getCurrentPassword().equals(passwordDecrypted) ) {
      throw new BadRequestException(I18nResolver.getMessage(I18nKeys.VALIDATION_PSWD_CURT_BAD), null);
    } else if(!StringUtils.isEmpty(userDto.getCurrentPassword()) && userDto.getCurrentPassword().equals(passwordDecrypted) && !userDto.getNewPassword().equals(passwordDecrypted) ) {
      user.setPassword(crypter.encrypt(userDto.getNewPassword()));
      final String masked = "*****%s";
      sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Password",
              String.format(masked, passwordDecrypted.substring(passwordDecrypted.length() - 3)),
              String.format(masked, userDto.getNewPassword().substring(userDto.getNewPassword().length() - 3))
      )).append(GeneralKeys.JUMP_LINE);
    }
    // Validacion de rol
    if( userDto.getRole() != null && !user.getRole().equals(user.getRole().getId()) ) {
      Role role = roleService.findEntityById(userDto.getRole());
      sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Rol",
              user.getRole().getName(), role.getName())).append(GeneralKeys.JUMP_LINE);
      user.setRole(role);
    }
    return sb.toString().trim();
  }

  private Page<User> findByFilter(String filter, Boolean active, Pageable pageable) {
    return repository.findAll((Specification<User>) (root, query, criteriaBuilder) -> getPredicateDinamycFilter(filter, active, criteriaBuilder, root), pageable);
  }

  private Predicate getPredicateDinamycFilter(String filter, Boolean active, CriteriaBuilder builder, Root<User> root) {

    List<Predicate> predicates = new ArrayList<>();

    if(!StringUtils.isEmpty(filter)) {
      String patternFilter = String.format(GeneralKeys.PATTERN_LIKE, filter.toLowerCase());
      Predicate pName = builder.like(builder.function(GeneralKeys.PG_FUNCTION_ACCENT, String.class, builder.lower(root.get(User.Fields.employee).get(Employee.Fields.name))), patternFilter);
      Predicate pSecondName = builder.like(builder.function(GeneralKeys.PG_FUNCTION_ACCENT, String.class, builder.lower(root.get(User.Fields.employee).get(Employee.Fields.secondName))), patternFilter);
      Predicate pSurname = builder.like(builder.function(GeneralKeys.PG_FUNCTION_ACCENT, String.class, builder.lower(root.get(User.Fields.employee).get(Employee.Fields.surname))), patternFilter);
      Predicate pSecondSurname = builder.like(builder.function(GeneralKeys.PG_FUNCTION_ACCENT, String.class, builder.lower(root.get(User.Fields.employee).get(Employee.Fields.secondSurname))), patternFilter);
      predicates.add(builder.or(pName, pSecondName, pSurname, pSecondSurname));
    }

    if(active != null) {
      predicates.add(builder.equal(root.get(User.Fields.active), active));
    }

    return builder.and(predicates.toArray(new Predicate[predicates.size()]));
  }

}
