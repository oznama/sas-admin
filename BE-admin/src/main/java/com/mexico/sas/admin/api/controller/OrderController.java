package com.mexico.sas.admin.api.controller;

import com.mexico.sas.admin.api.dto.order.OrderDto;
import com.mexico.sas.admin.api.dto.order.OrderFindDto;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.service.OrderService;
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
          @ApiResponse(code = 201, message = "Success", response = OrderDto.class)
  })
  public ResponseEntity<OrderDto> saveOrder(@Valid @RequestBody OrderDto orderDto) throws CustomException {
    log.info("Saving order");
    service.save(orderDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(service.findById(orderDto.getId()));
  }

  @PutMapping(path = "/{id}", headers = "Accept=application/json")
  @ResponseStatus(code = HttpStatus.CREATED)
  @ApiOperation(httpMethod = "PUT",
          value = "Servicio para actualizar ordenes de pago",
          nickname = "/updateOrder")
  @ApiResponses(value = {
          @ApiResponse(code = 201, message = "Success", response = OrderDto.class)
  })
  public ResponseEntity<OrderDto> updateOrder(@PathVariable("id") Long id, @Valid @RequestBody OrderDto orderDto) throws CustomException {
    log.info("Updating order");
    service.update(id, orderDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(orderDto);
  }

  @GetMapping("/byApplication/{projectApplicationId}")
  @ApiOperation(httpMethod = "GET",
          value = "Servicio para recuperar ordenes de aplicacion",
          nickname = "/findByProjectApplicationId")
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Success", response = OrderFindDto.class, responseContainer = "List")
  })
  public ResponseEntity<List<OrderFindDto>> findByProjectApplicationId(@PathVariable("projectApplicationId") Long projectApplicationId) throws CustomException {
    log.info("Finding orders by project application");
    return ResponseEntity.ok(service.findByProjectId(projectApplicationId));
  }

  @GetMapping("/{id}")
  @ApiOperation(httpMethod = "GET",
          value = "Servicio para recuperar orden de pago",
          nickname = "/findById")
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Success", response = OrderDto.class)
  })
  public ResponseEntity<OrderDto> findById(@PathVariable("id") Long id) throws CustomException {
    log.info("Finding project by id");
    return ResponseEntity.ok(service.findById(id));
  }

}
