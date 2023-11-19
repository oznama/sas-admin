package com.mexico.sas.admin.api.controller;

import com.mexico.sas.admin.api.dto.ResponseDto;
import com.mexico.sas.admin.api.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@ControllerAdvice
public class CustomExceptionHandler {

  @ExceptionHandler({LoginException.class})
  public ResponseEntity<ResponseDto> handleLoginException(LoginException e) {
    log.error("LoginException catched: {}", e.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new ResponseDto(HttpStatus.UNAUTHORIZED.value(), e.getMessage(), null));
  }

  @ExceptionHandler({BadRequestException.class})
  public ResponseEntity<ResponseDto> handleBadRequestException(BadRequestException e) {
    log.error("BadRequestException catched: {}", e.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ResponseDto(HttpStatus.BAD_REQUEST.value(), e.getMessage(), e.getDetail()));
  }

  @ExceptionHandler({NoContentException.class})
  public ResponseEntity<ResponseDto> handleNotFoundException(NoContentException e) {
    log.error("NoContentException catched", e);
    return ResponseEntity.status(HttpStatus.NO_CONTENT)
            .body(new ResponseDto(HttpStatus.NO_CONTENT.value(), e.getMessage(), null));
  }

  @ExceptionHandler({CustomException.class})
  public ResponseEntity<ResponseDto> handleCustomException(CustomException e) {
    log.error("CustomException catched: {}", e.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(new ResponseDto(HttpStatus.CONFLICT.value(), e.getMessage(), null));
  }

  @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
  public ResponseEntity<ResponseDto> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
    log.error("Http Request Method Not Supported: {}", e.getMessage());
    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
            .body(new ResponseDto(HttpStatus.METHOD_NOT_ALLOWED.value(), e.getMessage(), null));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ResponseDto> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
    log.error("Http Message Not Readable: {}", e.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ResponseDto(HttpStatus.BAD_REQUEST.value(), e.getMessage(), "Request"));
  }

  @ExceptionHandler({ InsufficientAuthenticationException.class })
  @ResponseBody
  public ResponseEntity<ResponseDto> handleInsufficientAuthenticationException(InsufficientAuthenticationException e) {
    log.error("Insufficient Authentication Exception: {}", e.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new ResponseDto(HttpStatus.UNAUTHORIZED.value(), e.getMessage(), "Security"));
  }

  @ExceptionHandler({Exception.class})
  public ResponseEntity<ResponseDto> handleException(Exception e) {
    log.error("Exception catched", e);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), e));
  }
}
