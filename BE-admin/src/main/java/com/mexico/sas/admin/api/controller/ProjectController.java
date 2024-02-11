package com.mexico.sas.admin.api.controller;

import com.mexico.sas.admin.api.dto.ResponseDto;
import com.mexico.sas.admin.api.dto.SelectDto;
import com.mexico.sas.admin.api.dto.project.*;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.i18n.I18nKeys;
import com.mexico.sas.admin.api.i18n.I18nResolver;
import com.mexico.sas.admin.api.service.ProjectService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/projects")
public class ProjectController {

  @Autowired
  private ProjectService service;

  @PostMapping(headers = "Accept=application/json")
  @ResponseStatus(code = HttpStatus.CREATED)
  @ApiOperation(httpMethod = "POST",
      value = "Servicio para crear proyecto",
      nickname = "/save")
  @ApiResponses(value = {
          @ApiResponse(code = 201, message = "Success", response = ProjectDto.class)
  })
  public ResponseEntity<ProjectDto> save(@Valid @RequestBody ProjectDto projectDto) throws CustomException {
    log.info("Saving project");
    service.save(projectDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(projectDto);
  }

  @PutMapping(path = "/{key}", headers = "Accept=application/json")
  @ResponseStatus(code = HttpStatus.OK)
  @ApiOperation(httpMethod = "PUT",
      value = "Servicio para actualizar proyecto",
      nickname = "/update/{key}")
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Success", response = ProjectUpdateDto.class)
  })
  public ResponseEntity<ProjectUpdateDto> update(@PathVariable("key") String key, @Valid @RequestBody ProjectUpdateDto projectUpdateDto) throws CustomException {
    log.info("Updating project");
    service.update(key, projectUpdateDto);
    return ResponseEntity.ok().body(projectUpdateDto);
  }

//  @PatchMapping("/{id}/lock")
//  @ResponseStatus(code = HttpStatus.OK)
//  @ApiOperation(httpMethod = "PATCH",
//          value = "Servicio para bloquear/desbloquear usuario",
//          nickname = "/lock")
//  public ResponseEntity<?> lock(@PathVariable("id") Long id, @RequestBody UserEnaDisDto userEnaDisDto) throws CustomException {
//    log.info("Changing user status");
//    return ResponseEntity.ok().body(service.setActive(id, userEnaDisDto.getLock()));
//  }

  @DeleteMapping(path = "/{key}")
  @ResponseStatus(code = HttpStatus.OK)
  @ApiOperation(httpMethod = "DELETE", value = "Servicio para eliminar proyecto", nickname = "delete")
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Success", response = ResponseDto.class)
  })
  public ResponseEntity<ResponseDto> delete(@PathVariable("key") String key) throws CustomException {
    log.info("Delete project");
    service.deleteLogic(key);
    return ResponseEntity.ok(
            new ResponseDto(HttpStatus.OK.value(), I18nResolver.getMessage(I18nKeys.GENERIC_MSG_OK), null));
  }

  @GetMapping("/{key}")
  @ApiOperation(httpMethod = "GET",
          value = "Servicio para recuperar proyecto por clave",
          nickname = "/findById")
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Success", response = ProjectFindDto.class)
  })
  public ResponseEntity<ProjectFindDto> findById(@PathVariable("key") String key) throws CustomException {
    log.info("Finding project by key");
    return ResponseEntity.ok(service.findByKey(key));
  }

  @GetMapping
  @ApiOperation(httpMethod = "GET",
          value = "Servicio para recuperar todos los proyectos",
          nickname = "/findAll")
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Success", response = ProjectPageableDto.class, responseContainer = "List")
  })
  public ResponseEntity<Page<ProjectPageableDto>> findAll(@RequestParam(required = false) String filter, Pageable pageable) throws CustomException {
    log.info("Finding all projects");
    return ResponseEntity.ok(service.findAll(filter, pageable));
  }

  @GetMapping("/select")
  @ApiOperation(httpMethod = "GET", value = "Servicio para recuperar proyectos para select", nickname = "/select")
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Success", response = SelectDto.class, responseContainer = "List") })
  public ResponseEntity<List<SelectDto>> select() throws CustomException {
    log.info("Getting catalog project");
    return ResponseEntity.ok(service.getForSelect());
  }

}
