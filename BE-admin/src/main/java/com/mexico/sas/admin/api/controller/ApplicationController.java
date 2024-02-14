package com.mexico.sas.admin.api.controller;

import com.mexico.sas.admin.api.dto.ResponseDto;
import com.mexico.sas.admin.api.dto.application.ApplicationDto;
import com.mexico.sas.admin.api.dto.application.ApplicationFindDto;
import com.mexico.sas.admin.api.dto.application.ApplicationPaggeableDto;
import com.mexico.sas.admin.api.dto.application.ApplicationUpdateDto;
import com.mexico.sas.admin.api.dto.company.CompanyPaggeableDto;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.i18n.I18nKeys;
import com.mexico.sas.admin.api.i18n.I18nResolver;
import com.mexico.sas.admin.api.service.ApplicationService;
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
@RequestMapping("/applications")
public class ApplicationController {
    @Autowired
    private ApplicationService service;

    @PostMapping(headers = "Accept=application/json")
    @ResponseStatus(code = HttpStatus.CREATED)
    @ApiOperation(httpMethod = "POST", value = "Servicio para crear aplicacion", nickname = "save")
    public ResponseEntity<ApplicationFindDto> save(@Valid @RequestBody ApplicationDto applicationDto)throws CustomException {
        log.info("Saving application");
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(applicationDto));
    }

    @PutMapping(path = "/{appName}", headers = "Accept=application/json")
    @ResponseStatus(code = HttpStatus.OK)
    @ApiOperation(httpMethod = "PUT", value = "Servicio para actualizar aplicacion", nickname = "update")
    public ResponseEntity<ApplicationUpdateDto> update(
            @PathVariable("appName") String appName, @Valid @RequestBody ApplicationUpdateDto applicationUpdateDto) throws CustomException {
        log.info("Updating application");
        service.update(appName, applicationUpdateDto);
        return ResponseEntity.ok().body(applicationUpdateDto);
    }

    @DeleteMapping(path = "/{appName}")
    @ResponseStatus(code = HttpStatus.OK)
    @ApiOperation(httpMethod = "DELETE", value = "Servicio para eliminar aplicacion", nickname = "delete")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = ResponseDto.class)
    })
    public ResponseEntity<ResponseDto> delete(@PathVariable("appName") String name) throws CustomException {
        log.info("Delete application");
        service.deleteLogic(name);
        return ResponseEntity.ok(
                new ResponseDto(HttpStatus.OK.value(), I18nResolver.getMessage(I18nKeys.GENERIC_MSG_OK), null));
    }

    @GetMapping("/{appName}")
    @ResponseStatus(code = HttpStatus.OK)
    @ApiOperation(httpMethod = "GET", value = "Servicio para recuperar aplicacion por nombre", nickname = "update")
    public ResponseEntity<ApplicationFindDto> findByName(@PathVariable("appName") String name) throws CustomException{
        log.info("Finding application by name");
        return ResponseEntity.ok(service.findByName(name));
    }

    @GetMapping()
    @ApiOperation(httpMethod = "GET", value = "Servicio para recuperar aplicaciones por filtro", nickname = "/findAll")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = CompanyPaggeableDto.class, responseContainer = "List") })
    public ResponseEntity<Page<ApplicationPaggeableDto>> findAll (@RequestParam(required = false) String filter,
                                                                  @RequestParam(required = false) Long type,
                                                                  Pageable pageable) throws CustomException{
        log.info("Getting all applications");
        return ResponseEntity.ok(service.findAll(filter, type, pageable));
    }

}
