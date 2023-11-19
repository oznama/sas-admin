package com.mexico.sas.admin.api.controller;

import com.mexico.sas.admin.api.dto.*;
import com.mexico.sas.admin.api.dto.catalog.CatalogDto;
import com.mexico.sas.admin.api.dto.catalog.CatalogPaggedDto;
import com.mexico.sas.admin.api.dto.catalog.CatalogUpdateDto;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.i18n.I18nKeys;
import com.mexico.sas.admin.api.i18n.I18nResolver;
import com.mexico.sas.admin.api.service.CatalogService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/internal")
public class InternalController {

  @Autowired
  private CatalogService service;

  @PostMapping(headers = "Accept=application/json")
  @ResponseStatus(code = HttpStatus.CREATED)
  @ApiOperation(httpMethod = "POST", value = "Servicio para crear catalogo", nickname = "save")
  public ResponseEntity<CatalogDto> save(@Valid @RequestBody CatalogDto catalogDto) throws CustomException {
    log.info("Saving catalog");
    return ResponseEntity.status(HttpStatus.CREATED).body(service.save(catalogDto));
  }

  @PutMapping(path = "/{id}", headers = "Accept=application/json")
  @ResponseStatus(code = HttpStatus.OK)
  @ApiOperation(httpMethod = "PUT", value = "Servicio para actualizar catalogo", nickname = "update")
  public ResponseEntity<CatalogDto> update(
          @PathVariable("id") Long id, @Valid @RequestBody CatalogUpdateDto catalogDto) throws CustomException {
    log.info("Updating catalog");
    return ResponseEntity.ok().body(service.update(id, catalogDto));
  }

  @PatchMapping
  @ResponseStatus(code = HttpStatus.OK)
  @ApiOperation(httpMethod = "PATCH", value = "Servicio para actualizar status de catalogo", nickname = "updateStatus")
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Success", response = ResponseDto.class)
  })
  public ResponseEntity<ResponseDto> updateStatus(@Valid @RequestBody UpdateStatusDto updateStatusDto) throws CustomException {
    log.info("Updating catalog's status");
    service.updateStatus(updateStatusDto);
    return ResponseEntity.ok(
            new ResponseDto(HttpStatus.OK.value(), I18nResolver.getMessage(I18nKeys.GENERIC_MSG_OK), updateStatusDto));
  }

  @GetMapping("/{id}")
  @ApiIgnore
  @ApiOperation(httpMethod = "GET", value = "Servicio para recuperar catalogo por id", nickname = "findById")
  public ResponseEntity<CatalogDto> findById(@PathVariable("id") Long id) throws CustomException {
    log.info("Finding catalog by id");
    return ResponseEntity.ok(service.findById(id));
  }

  @GetMapping("/{parent}/{id}")
  @ApiOperation(
          httpMethod = "GET", value = "Servicio para recuperar catalogo por id y padre", nickname = "findbyIdAndParent")
  public ResponseEntity<CatalogDto> findbyIdAndParent(
          @PathVariable("id") Long id, @PathVariable("parent") Long parent) throws CustomException {
    log.info("Finding catalog by id and parent");
    return ResponseEntity.ok(service.findByIdAndCatalogParent(id, parent));
  }

  @GetMapping("/{id}/childs")
  @ApiOperation(httpMethod = "GET", value = "Servicio para recuperar los items", nickname = "/findChilds")
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Success", response = CatalogPaggedDto.class, responseContainer = "List")
  })
  public ResponseEntity<List<CatalogDto>> findChilds(@PathVariable("id") Long id) throws CustomException {
    log.info("Finding catalog items by id");
    return ResponseEntity.ok(service.findChildsDto(id));
  }

  @GetMapping
  @ApiOperation(httpMethod = "GET", value = "Servicio para recuperar los catalogs principales", nickname = "/findAll")
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Success", response = CatalogDto.class, responseContainer = "List") })
  public ResponseEntity<List<CatalogDto>> findAll() throws CustomException {
    log.info("Finding catalog parents");
    return ResponseEntity.ok(service.findAll());
  }

}
