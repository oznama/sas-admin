package com.mexico.sas.admin.api.controller;

import com.mexico.sas.admin.api.dto.project.*;
import com.mexico.sas.admin.api.exception.CustomException;
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

  @PutMapping(path = "/{id}", headers = "Accept=application/json")
  @ResponseStatus(code = HttpStatus.OK)
  @ApiOperation(httpMethod = "PUT",
      value = "Servicio para actualizar proyecto",
      nickname = "/update/{id}")
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Success", response = ProjectUpdateDto.class)
  })
  public ResponseEntity<ProjectUpdateDto> update(@PathVariable("id") Long id, @Valid @RequestBody ProjectUpdateDto projectUpdateDto) throws CustomException {
    log.info("Updating project");
    service.update(id, projectUpdateDto);
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

//  @DeleteMapping("/{id}")
//  @ResponseStatus(code = HttpStatus.OK)
//  @ApiOperation(httpMethod = "DELETE",
//          value = "Servicio para eliminar usuario",
//          nickname = "delete")
//  public ResponseEntity<Long> deleteLogic(@PathVariable("id") Long id) throws CustomException {
//    log.info("Deleting user");
//    service.deleteLogic(id);
//    return ResponseEntity.ok(id);
//  }

  @GetMapping("/{id}")
  @ApiOperation(httpMethod = "GET",
          value = "Servicio para recuperar proyecto por id",
          nickname = "/findById")
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Success", response = ProjectFindDto.class)
  })
  public ResponseEntity<ProjectFindDto> findById(@PathVariable("id") Long id) throws CustomException {
    log.info("Finding project by id");
    return ResponseEntity.ok(service.findById(id));
  }

  @GetMapping
  @ApiOperation(httpMethod = "GET",
          value = "Servicio para recuperar todos los proyectos",
          nickname = "/findAll")
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Success", response = ProjectPageableDto.class, responseContainer = "List")
  })
  public ResponseEntity<Page<ProjectPageableDto>> findAll(Pageable pageable) throws CustomException {
    log.info("Finding all projects");
    return ResponseEntity.ok(service.findAll(pageable));
  }

  @PostMapping(path = "/application", headers = "Accept=application/json")
  @ResponseStatus(code = HttpStatus.CREATED)
  @ApiOperation(httpMethod = "POST",
          value = "Servicio para crear aplicacion de proyecto",
          nickname = "/saveApplication")
  @ApiResponses(value = {
          @ApiResponse(code = 201, message = "Success", response = ProjectApplicationDto.class)
  })
  public ResponseEntity<ProjectApplicationDto> saveApplication(@Valid @RequestBody ProjectApplicationDto projectApplicationDto) throws CustomException {
    log.info("Saving project");
    service.save(projectApplicationDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(projectApplicationDto);
  }

  @GetMapping("/{projectId}/application/{id}")
  @ApiOperation(httpMethod = "GET",
          value = "Servicio para recuperar aplicacion de proyecto por id",
          nickname = "/findApplicationById")
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Success", response = ProjectApplicationDto.class)
  })
  public ResponseEntity<ProjectApplicationDto> findById(@PathVariable("projectId") Long projectId,
                                                        @PathVariable("id") Long applicationId) throws CustomException {
    log.info("Finding project by id");
    return ResponseEntity.ok(service.findById(projectId, applicationId));
  }

}
