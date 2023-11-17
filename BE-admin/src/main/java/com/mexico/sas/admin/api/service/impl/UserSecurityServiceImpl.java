package com.mexico.sas.admin.api.service.impl;

import com.mexico.sas.admin.api.dto.user.security.UserSecurityPasswordDto;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.model.User;
import com.mexico.sas.admin.api.repository.UserSecurityRepository;
import com.mexico.sas.admin.api.security.Crypter;
import com.mexico.sas.admin.api.service.CatalogService;
import com.mexico.sas.admin.api.service.UserSecurityService;
import com.mexico.sas.admin.api.service.UserService;
import com.mexico.sas.admin.api.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserSecurityServiceImpl extends Utils implements UserSecurityService {

  @Autowired
  private UserSecurityRepository repository;

  @Autowired
  private UserService userService;

  @Autowired
  private CatalogService catalogService;

  @Autowired
  private Crypter crypter;

  @Override
  public void setPassword(Long id, UserSecurityPasswordDto userSecurityPasswordDto) throws CustomException {
    User user = userService.findEntityById(id);
    try {
      log.debug("Setting password to user {}-{}", user.getId(), user.getEmail());
      repository.setPassword(user.getUserSecurity().getId(), crypter.encrypt(userSecurityPasswordDto.getPassword()));
    } catch (Exception e) {
      throw new CustomException(e.getMessage());
    }
  }
}
