package com.mexico.sas.admin.api.service.impl;

import com.mexico.sas.admin.api.constants.CatalogKeys;
import com.mexico.sas.admin.api.constants.GeneralKeys;
import com.mexico.sas.admin.api.dto.user.security.RetrivePasswordDto;
import com.mexico.sas.admin.api.dto.user.security.UserSecurityPasswordDto;
import com.mexico.sas.admin.api.exception.BadRequestException;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.model.User;
import com.mexico.sas.admin.api.model.UserConfirmationToken;
import com.mexico.sas.admin.api.repository.UserConfirmationTokenRepository;
import com.mexico.sas.admin.api.repository.UserRepository;
import com.mexico.sas.admin.api.repository.UserSecurityRepository;
import com.mexico.sas.admin.api.security.Crypter;
import com.mexico.sas.admin.api.i18n.I18nKeys;
import com.mexico.sas.admin.api.i18n.I18nResolver;
import com.mexico.sas.admin.api.service.CatalogService;
import com.mexico.sas.admin.api.service.UserSecurityService;
import com.mexico.sas.admin.api.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class UserSecurityServiceImpl extends Utils implements UserSecurityService {

  @Autowired
  private UserSecurityRepository repository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserConfirmationTokenRepository userConfirmationTokenRepository;

  @Autowired
  private CatalogService catalogService;

  @Autowired
  private Crypter crypter;

  @Override
  public void setPassword(String authorization, UserSecurityPasswordDto userSecurityPasswordDto) throws CustomException {
    User user = getUser(authorization.replace(GeneralKeys.CONSTANT_BEARER, ""));
    try {
      log.debug("Setting password to user {}-{}", user.getId(), user.getEmail());
      repository.setPassword(user.getUserSecurity().getId(), crypter.encrypt(userSecurityPasswordDto.getPassword()));
      log.debug("Deleting token confirmation {}", user.getUserConfirmationToken().getId());
      user.getUserConfirmationToken().setUsed(Boolean.TRUE);
      userConfirmationTokenRepository.save(user.getUserConfirmationToken());
    } catch (Exception e) {
      throw new CustomException(e.getMessage());
    }
  }

  @Override
  public void recoveryPassword(RetrivePasswordDto retrivePasswordDto, String locale) throws CustomException {
    User user = userRepository.findByEmailIgnoreCaseAndEliminateFalse(retrivePasswordDto.getEmail())
            .orElseThrow(() -> new BadRequestException(I18nResolver
                    .getMessage(I18nKeys.USER_SECURITY_NOT_FOUND, retrivePasswordDto.getEmail()), retrivePasswordDto));

    UserConfirmationToken userConfirmationToken = null;
    try {
      userConfirmationToken = userConfirmationTokenRepository.findByUser(user).orElseThrow(() -> new CustomException(""));
      // Si ya existe un token, se actualiza y se hace vigente
      userConfirmationToken.update();
      userConfirmationTokenRepository.save(userConfirmationToken);
    } catch (CustomException e) {
      userConfirmationToken = new UserConfirmationToken(user);
      userConfirmationTokenRepository.save(userConfirmationToken);
    }
  }

  @Override
  public String validateEmail(String token, String locale) throws CustomException {
    StringBuilder urlError = new StringBuilder(catalogService.findById(CatalogKeys.URL_FRONT_LINK_EXPIRED).getValue());
    UserConfirmationToken userConfirmationToken = userConfirmationTokenRepository
            .findByConfirmationToken(token).orElse(null);
    String urlResponse = null;
    if(userConfirmationToken == null || userConfirmationToken.getId() == null){ 
      urlResponse = urlError.append("?").append(GeneralKeys.QUERY_PARAM_MESSAGE).append("=")
              .append(I18nResolver.getMessage(I18nKeys.USER_SECURITY_TOKEN_NOT_FOUND)).toString();
    } else if (userConfirmationToken.getUsed()) {
      urlResponse = urlError.append("?").append(GeneralKeys.QUERY_PARAM_MESSAGE).append("=")
              .append(I18nResolver.getMessage(I18nKeys.USER_SECURITY_TOKEN_USED)).toString();
    } else if (userConfirmationToken.getExpired()) {
      urlResponse = urlError.append("?").append(GeneralKeys.QUERY_PARAM_MESSAGE).append("=")
              .append(I18nResolver.getMessage(I18nKeys.USER_SECURITY_TOKEN_EXPIRED)).toString();
    }
    if(!StringUtils.isEmpty(urlResponse)) {
      log.debug("Url to redirect by token error\n{}", urlResponse);
      return urlResponse;
    }
    StringBuilder urlToRedirect = new StringBuilder(catalogService.findById(CatalogKeys.URL_FRONT_USER_SETPSWD).getValue());
    urlToRedirect.append("?").append(GeneralKeys.QUERY_PARAM_TOKEN).append("=").append(token);
    log.debug("Redirect to {}", urlToRedirect);
    return urlToRedirect.toString();
  }

  private User getUser(String token) throws CustomException {
    UserConfirmationToken userConfirmationToken = userConfirmationTokenRepository
            .findByConfirmationTokenAndUsedFalse(token)
            .orElseThrow( () -> new CustomException(I18nResolver.getMessage(I18nKeys.USER_SECURITY_TOKEN_NOT_FOUND)) );
    return userConfirmationToken.getUser();
  }
}
