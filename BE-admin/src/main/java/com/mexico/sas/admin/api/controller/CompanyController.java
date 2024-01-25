package com.mexico.sas.admin.api.controller;

import com.mexico.sas.admin.api.dto.ResponseDto;
import com.mexico.sas.admin.api.dto.company.*;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.i18n.I18nKeys;
import com.mexico.sas.admin.api.i18n.I18nResolver;
import com.mexico.sas.admin.api.service.CompanyService;
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
@RequestMapping("/companies")
public class CompanyController {

  @Autowired
  private CompanyService service;

  @PostMapping(headers = "Accept=application/json")
  @ResponseStatus(code = HttpStatus.CREATED)
  @ApiOperation(httpMethod = "POST", value = "Servicio para crear compania", nickname = "save")
  public ResponseEntity<CompanyFindDto> save(@Valid @RequestBody CompanyDto companyDto) throws CustomException {
    log.info("Saving company");
    return ResponseEntity.status(HttpStatus.CREATED).body(service.save(companyDto));
  }

  @PutMapping(path = "/{id}", headers = "Accept=application/json")
  @ResponseStatus(code = HttpStatus.OK)
  @ApiOperation(httpMethod = "PUT", value = "Servicio para actualizar compania", nickname = "update")
  public ResponseEntity<CompanyUpdateDto> update(
          @PathVariable("id") Long id, @Valid @RequestBody CompanyUpdateDto companyUpdateDto) throws CustomException {
    log.info("Updating company");
    return ResponseEntity.ok().body(service.update(id, companyUpdateDto));
  }

  @DeleteMapping(path = "/{id}")
  @ResponseStatus(code = HttpStatus.OK)
  @ApiOperation(httpMethod = "DELETE", value = "Servicio para eliminar compania", nickname = "delete")
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Success", response = ResponseDto.class)
  })
  public ResponseEntity<ResponseDto> delete(@PathVariable("id") Long id) throws CustomException {
    log.info("Delete employee");
    service.deleteLogic(id);
    return ResponseEntity.ok(
            new ResponseDto(HttpStatus.OK.value(), I18nResolver.getMessage(I18nKeys.GENERIC_MSG_OK), null));
  }

  @GetMapping("/{id}")
  @ResponseStatus(code = HttpStatus.OK)
  @ApiOperation(httpMethod = "GET", value = "Servicio para recuperar compania por id", nickname = "update")
  public ResponseEntity<CompanyFindDto> findById(@PathVariable("id") Long id) throws CustomException {
    log.info("Finding catalog by id");
    return ResponseEntity.ok(service.findById(id));
  }

  @GetMapping()
  @ApiOperation(httpMethod = "GET", value = "Servicio para recuperar companias por filtro", nickname = "/findAll")
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Success", response = CompanyPaggeableDto.class, responseContainer = "List") })
  public ResponseEntity<Page<CompanyPaggeableDto>> findAll(@RequestParam(required = false) String filter,
                                                           @RequestParam(required = false) Long type,
                                                           Pageable pageable) throws CustomException {
    log.info("Getting all companies");
    return ResponseEntity.ok(service.findAll(filter, type, pageable));
  }

  @GetMapping("/select")
  @ApiOperation(httpMethod = "GET", value = "Servicio para recuperar companias para select", nickname = "/select")
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Success", response = CompanyFindSelectDto.class, responseContainer = "List") })
  public ResponseEntity<List<CompanyFindSelectDto>> select() throws CustomException {
    log.info("Getting catalog company");
    return ResponseEntity.ok(service.getForSelect());
  }

}
