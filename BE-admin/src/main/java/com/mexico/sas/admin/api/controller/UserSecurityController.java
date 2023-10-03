package com.mexico.sas.admin.api.controller;

import com.mexico.sas.admin.api.dto.RetrivePasswordDto;
import com.mexico.sas.admin.api.dto.UserSecurityPasswordDto;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.service.UserSecurityService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/users/security")
public class  UserSecurityController {

  @Autowired
  private UserSecurityService service;

  @PatchMapping("/setpswd")
  @ResponseStatus(code = HttpStatus.OK)
  @ApiOperation(httpMethod = "PATCH",
          value = "Servicio para asignar password",
          nickname = "/setpswd")
  public ResponseEntity<?> setPassword(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
                                       @Valid @RequestBody UserSecurityPasswordDto userSecurityPasswordDto) throws CustomException {
    log.info("Setting user password");
    service.setPassword(authorization, userSecurityPasswordDto);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/confirm-account")
  @ResponseStatus(code = HttpStatus.OK)
  public ResponseEntity<?> confirmAccount(@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) String language,
                                          @RequestParam String token) throws CustomException {
    log.info("Confirm account...");
    String urlRedirect = service.validateEmail(token, language).replaceAll(" ", "%20");
    return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(urlRedirect)).build();
  }

  @PostMapping("/recovery")
  @ResponseStatus(code = HttpStatus.OK)
  @ApiOperation(httpMethod = "POST",
          value = "Servicio para recuperar contraseña de usuario",
          nickname = "recovery")
  public ResponseEntity<?> recoveryPassword(@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) String language,
                                            @Valid @RequestBody RetrivePasswordDto retrivePasswordDto) throws CustomException {
    log.info("Recovering user password");
    service.recoveryPassword(retrivePasswordDto, language);
    return ResponseEntity.ok().build();
  }
}
