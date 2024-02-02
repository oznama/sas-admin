package com.mexico.sas.admin.api.controller;

import com.mexico.sas.admin.api.dto.ResponseDto;
import com.mexico.sas.admin.api.dto.SelectDto;
import com.mexico.sas.admin.api.dto.order.OrderDto;
import com.mexico.sas.admin.api.dto.order.OrderFindDto;
import com.mexico.sas.admin.api.dto.order.OrderPaggeableDto;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.i18n.I18nKeys;
import com.mexico.sas.admin.api.i18n.I18nResolver;
import com.mexico.sas.admin.api.service.OrderService;
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
@RequestMapping("/orders")
public class OrderController {

  @Autowired
  private OrderService service;

  @PostMapping(headers = "Accept=application/json")
  @ResponseStatus(code = HttpStatus.CREATED)
  @ApiOperation(httpMethod = "POST",
          value = "Servicio para crear ordenes de pago",
          nickname = "/saveOrder")
  @ApiResponses(value = {
          @ApiResponse(code = 201, message = "Success", response = OrderFindDto.class)
  })
  public ResponseEntity<OrderFindDto> saveOrder(@Valid @RequestBody OrderDto orderDto) throws CustomException {
    log.info("Saving order");
    service.save(orderDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(service.findById(orderDto.getId()));
  }

  @PutMapping(path = "/{id}", headers = "Accept=application/json")
  @ResponseStatus(code = HttpStatus.OK)
  @ApiOperation(httpMethod = "PUT",
          value = "Servicio para actualizar ordenes de pago",
          nickname = "/updateOrder")
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Success", response = OrderDto.class)
  })
  public ResponseEntity<OrderDto> updateOrder(@PathVariable("id") Long id, @Valid @RequestBody OrderDto orderDto) throws CustomException {
    log.info("Updating order");
    service.update(id, orderDto);
    return ResponseEntity.ok().body(orderDto);
  }

  @DeleteMapping(path = "/{id}")
  @ResponseStatus(code = HttpStatus.OK)
  @ApiOperation(httpMethod = "DELETE", value = "Servicio para eliminar desactivar orden", nickname = "delete")
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Success", response = ResponseDto.class)
  })
  public ResponseEntity<ResponseDto> delete(@PathVariable("id") Long id) throws CustomException {
    log.info("Delete order");
    service.deleteLogic(id);
    return ResponseEntity.ok(
            new ResponseDto(HttpStatus.OK.value(), I18nResolver.getMessage(I18nKeys.GENERIC_MSG_OK), null));
  }

  @GetMapping("/byProject/{projectId}")
  @ApiOperation(httpMethod = "GET",
          value = "Servicio para recuperar ordenes de proyecto",
          nickname = "/byProject")
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Success", response = OrderPaggeableDto.class, responseContainer = "List")
  })
  public ResponseEntity<List<OrderPaggeableDto>> findByProjectId(@PathVariable("projectId") Long projectId) throws CustomException {
    log.info("Finding orders by project");
    return ResponseEntity.ok(service.findByProjectId(projectId));
  }

  @GetMapping("/{id}")
  @ApiOperation(httpMethod = "GET",
          value = "Servicio para recuperar orden de pago",
          nickname = "/findById")
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Success", response = OrderFindDto.class)
  })
  public ResponseEntity<OrderFindDto> findById(@PathVariable("id") Long id) throws CustomException {
    log.info("Finding project by id");
    return ResponseEntity.ok(service.findById(id));
  }

  @GetMapping
  @ApiOperation(httpMethod = "GET",
          value = "Servicio para recuperar todas las ordenes",
          nickname = "/findAll")
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Success", response = OrderPaggeableDto.class, responseContainer = "List")
  })
  public ResponseEntity<Page<OrderPaggeableDto>> findAll(@RequestParam(required = false) String filter, Pageable pageable) throws CustomException {
    log.info("Finding all orders");
    return ResponseEntity.ok(service.findAll(filter, pageable));
  }

  @GetMapping("/select")
  @ApiOperation(httpMethod = "GET", value = "Servicio para recuperar ordenes para select", nickname = "/select")
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Success", response = SelectDto.class, responseContainer = "List") })
  public ResponseEntity<List<SelectDto>> select() throws CustomException {
    log.info("Getting catalog order");
    return ResponseEntity.ok(service.getForSelect());
  }

}
