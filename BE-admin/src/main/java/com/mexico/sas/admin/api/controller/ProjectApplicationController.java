package com.mexico.sas.admin.api.controller;

import com.mexico.sas.admin.api.dto.ResponseDto;
import com.mexico.sas.admin.api.dto.project.*;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.i18n.I18nKeys;
import com.mexico.sas.admin.api.i18n.I18nResolver;
import com.mexico.sas.admin.api.service.ProjectApplicationService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/projects/application")
public class ProjectApplicationController {

  @Autowired
  private ProjectApplicationService service;

  @PostMapping(headers = "Accept=application/json")
  @ResponseStatus(code = HttpStatus.CREATED)
  @ApiOperation(httpMethod = "POST",
          value = "Servicio para crear aplicacion de proyecto",
          nickname = "/saveApplication")
  @ApiResponses(value = {
          @ApiResponse(code = 201, message = "Success", response = ProjectApplicationFindDto.class)
  })
  public ResponseEntity<ProjectApplicationFindDto> saveApplication(@Valid @RequestBody ProjectApplicationDto projectApplicationDto) throws CustomException {
    log.info("Saving project application");
    service.save(projectApplicationDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(service.findByApplicationId(projectApplicationDto.getId()));
  }

  @PutMapping(path = "/{id}", headers = "Accept=application/json")
  @ResponseStatus(code = HttpStatus.CREATED)
  @ApiOperation(httpMethod = "PUT",
          value = "Servicio para actualizar aplicacion de proyecto",
          nickname = "/updateApplication")
  @ApiResponses(value = {
          @ApiResponse(code = 201, message = "Success", response = ProjectApplicationUpdateDto.class)
  })
  public ResponseEntity<ProjectApplicationUpdateDto> updateApplication(@PathVariable("id") Long id, @Valid @RequestBody ProjectApplicationUpdateDto projectApplicationDto) throws CustomException {
    log.info("Updating project application");
    service.update(id, projectApplicationDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(projectApplicationDto);
  }

  @DeleteMapping(path = "/{id}")
  @ResponseStatus(code = HttpStatus.OK)
  @ApiOperation(httpMethod = "DELETE", value = "Servicio para eliminar aplicacion", nickname = "delete")
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Success", response = ResponseDto.class)
  })
  public ResponseEntity<ResponseDto> delete(@PathVariable("id") Long id) throws CustomException {
    log.info("Delete project");
    service.deleteLogic(id);
    return ResponseEntity.ok(
            new ResponseDto(HttpStatus.OK.value(), I18nResolver.getMessage(I18nKeys.GENERIC_MSG_OK), null));
  }

  @GetMapping("/{projectId}")
  @ApiOperation(httpMethod = "GET",
          value = "Servicio para recuperar aplicaciones de proyecto por id",
          nickname = "/findApplicationById")
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Success", response = ProjectApplicationPaggeableDto.class, responseContainer = "List")
  })
  public ResponseEntity<List<ProjectApplicationPaggeableDto>> findByProjectId(@PathVariable("projectId") Long projectId) throws CustomException {
    log.info("Finding project by id");
    return ResponseEntity.ok(service.findByProjectId(projectId));
  }

  @GetMapping("/{projectId}/{id}")
  @ApiOperation(httpMethod = "GET",
          value = "Servicio para recuperar aplicacion de proyecto por id",
          nickname = "/findApplicationById")
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Success", response = ProjectApplicationDto.class)
  })
  public ResponseEntity<ProjectApplicationDto> findByApplicationId(@PathVariable("projectId") Long projectId,
                                                        @PathVariable("id") Long id) throws CustomException {
    log.info("Finding project by id");
    return ResponseEntity.ok(service.findByProjectAndId(projectId, id));
  }

}
