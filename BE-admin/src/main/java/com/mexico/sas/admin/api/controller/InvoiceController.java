package com.mexico.sas.admin.api.controller;

import com.mexico.sas.admin.api.dto.invoice.InvoiceDto;
import com.mexico.sas.admin.api.dto.invoice.InvoiceFindDto;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.service.InvoiceService;
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
@RequestMapping("/invoices")
public class InvoiceController {

  @Autowired
  private InvoiceService service;

  @PostMapping(headers = "Accept=application/json")
  @ResponseStatus(code = HttpStatus.CREATED)
  @ApiOperation(httpMethod = "POST",
          value = "Servicio para crear factura",
          nickname = "/saveInvoice")
  @ApiResponses(value = {
          @ApiResponse(code = 201, message = "Success", response = InvoiceDto.class)
  })
  public ResponseEntity<InvoiceDto> saveInvoice(@Valid @RequestBody InvoiceDto invoiceDto) throws CustomException {
    log.info("Saving invoice");
    service.save(invoiceDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(service.findById(invoiceDto.getId()));
  }

  @PutMapping(path = "/{id}", headers = "Accept=application/json")
  @ResponseStatus(code = HttpStatus.CREATED)
  @ApiOperation(httpMethod = "PUT",
          value = "Servicio para actualizar factura",
          nickname = "/updateInvoice")
  @ApiResponses(value = {
          @ApiResponse(code = 201, message = "Success", response = InvoiceDto.class)
  })
  public ResponseEntity<InvoiceDto> updateInvoice(@PathVariable("id") Long id, @Valid @RequestBody InvoiceDto invoiceDto) throws CustomException {
    log.info("Updating invoice");
    service.update(id, invoiceDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(invoiceDto);
  }

  @GetMapping("/byOrder/{orderId}")
  @ApiOperation(httpMethod = "GET",
          value = "Servicio para recuperar facturas de orden",
          nickname = "/findByOrderId")
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Success", response = InvoiceFindDto.class, responseContainer = "List")
  })
  public ResponseEntity<List<InvoiceFindDto>> findByOrderId(@PathVariable("orderId") Long orderId) throws CustomException {
    log.info("Finding invoices by order");
    return ResponseEntity.ok(service.findByOrderId(orderId));
  }

  @GetMapping("/{id}")
  @ApiOperation(httpMethod = "GET",
          value = "Servicio para recuperar factura",
          nickname = "/findById")
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Success", response = InvoiceDto.class)
  })
  public ResponseEntity<InvoiceDto> findById(@PathVariable("id") Long id) throws CustomException {
    log.info("Finding project by id");
    return ResponseEntity.ok(service.findById(id));
  }

  @GetMapping
  @ApiOperation(httpMethod = "GET",
          value = "Servicio para recuperar todas las facturas",
          nickname = "/findAll")
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Success", response = InvoiceFindDto.class, responseContainer = "List")
  })
  public ResponseEntity<Page<InvoiceFindDto>> findAll(@RequestParam(required = false) String filter, Pageable pageable) throws CustomException {
    log.info("Finding all orders");
    return ResponseEntity.ok(service.findAll(filter, pageable));
  }

}
