package com.mexico.sas.admin.api.security;

import com.mexico.sas.admin.api.constants.GeneralKeys;
import com.mexico.sas.admin.api.dto.SecurityContextPrincipal;
import com.mexico.sas.admin.api.exception.AuthenticationException;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.model.Employee;
import com.mexico.sas.admin.api.model.User;
import com.mexico.sas.admin.api.service.EmployeeService;
import com.mexico.sas.admin.api.service.UserService;
import com.mexico.sas.admin.api.util.Utils;
import io.jsonwebtoken.Claims;

import java.io.IOException;
import java.util.*;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
public class AuthorizationFilter extends OncePerRequestFilter {

  public static Locale LOCALE = Locale.US;

  @Autowired
  private EmployeeService employeeService;

  @Autowired
  private UserService userService;

  @Autowired
  private CustomJWT customJWT;

  private String requestUri;
  private String method;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    requestUri = request.getRequestURI();
    method = request.getMethod();
    setLocale(request.getHeader(HttpHeaders.ACCEPT_LANGUAGE));
    log.debug("Filter auth for url [{}] with method: {} ...", requestUri, method);
    try {
      validateSecurity(request);
      filterChain.doFilter(request, response);
    } catch (AuthenticationException e) {
      log.error("User not authorized, cause: {}", e.getMessage());
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
      clearSecurityContext(e.getMessage());
    }
  }

  private void validateSecurity(HttpServletRequest request) throws AuthenticationException {
    log.debug("Validiting token ...");
    String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    if(StringUtils.hasText(authHeader) && authHeader.startsWith(GeneralKeys.CONSTANT_BEARER)) {
      log.debug("Request has token!");
      Claims claims = customJWT.getClaims(authHeader.replace(GeneralKeys.CONSTANT_BEARER, ""));
      String email = claims.getSubject();
      try {
        Employee employee = employeeService.findEntityByEmail(email);
        User user = userService.findEntityByEmployeeId(employee.getId());
        SecurityContextPrincipal principal = new SecurityContextPrincipal();
        principal.setUserId(user.getId());
        principal.setName(Utils.getFullname(employee));
        principal.setRoleId(user.getRole().getId());
        principal.setCompanyId(employee.getCompanyId());
        principal.setEmployeeId(employee.getId());
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(principal, user.getPassword(), null);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        log.debug("Token valid!");
      } catch (CustomException e) {
        throw new AuthenticationException("User not valid");
      }
    } else
      clearSecurityContext("Request not has token");
  }

  private void clearSecurityContext(String msgDebug) {
    log.warn("Clear security context: {}", msgDebug);
    SecurityContextHolder.clearContext();
  }

  private void setLocale(String acceptLanguage) {
    log.debug("aceptLanguage: {}", acceptLanguage);
    try {
      LOCALE = Locale.forLanguageTag(acceptLanguage);
    } catch (NullPointerException e) {
      log.warn("Header language not set!");
      LOCALE = Locale.US;
    }
    log.debug("Locale: {} {} {} {}", LOCALE.getLanguage(), LOCALE.getCountry(), LOCALE.getDisplayName(), LOCALE.getDisplayLanguage());
  }

}
