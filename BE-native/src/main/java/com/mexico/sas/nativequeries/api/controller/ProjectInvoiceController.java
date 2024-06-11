package com.mexico.sas.nativequeries.api.controller;

import com.mexico.sas.nativequeries.api.model.ProjectWithoutInvoices;
import com.mexico.sas.nativequeries.api.service.ProjectInvoiceService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("projects/withoutinvoices")
public class ProjectInvoiceController {

    @Autowired
    private ProjectInvoiceService projectInvoiceService;

    @GetMapping
    @ApiOperation(httpMethod = "GET", value = "Servicio para recuperar proyectos sin facturas", nickname = "findProjectsWithoutInvoices")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = ProjectWithoutInvoices.class, responseContainer = "List") })
    public ResponseEntity<Page<ProjectWithoutInvoices>> findProjectsWithoutInvoices(@RequestParam(required = false) String filter,
                                                                                    @RequestParam(defaultValue = "1") int report,
                                                                                    @RequestParam(defaultValue = "false") Boolean orderCanceled,
                                                                                    @RequestParam(defaultValue = "30") int percentage,
                                                                                    @RequestParam(defaultValue = "0") int page,
                                                                                    @RequestParam(defaultValue = "10") int size) {
        log.info("Finding projects without invoices");
        return ResponseEntity.ok(projectInvoiceService.findProjectsWithoutInvoices(filter, report, orderCanceled, percentage, page, size));
    }

    @GetMapping("keys")
    @ApiOperation(httpMethod = "GET", value = "Servicio para recuperar claves de proyectos sin facturas", nickname = "findProjectsKeysWithoutInvoices")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = String.class, responseContainer = "List") })
    public ResponseEntity<List<String>> findProjectsKeysWithoutInvoices(@RequestParam(required = false) String filter,
                                                                                    @RequestParam(defaultValue = "1") int report,
                                                                                    @RequestParam(defaultValue = "false") Boolean orderCanceled,
                                                                                    @RequestParam(defaultValue = "30") int percentage) {
        log.info("Finding projects keys without invoices");
        return ResponseEntity.ok(projectInvoiceService.findProjectsWithoutInvoices(filter, report, orderCanceled, percentage));
    }

    @GetMapping("export")
    @ApiOperation(httpMethod = "GET", value = "Servicio para exportar proyectos sin facturas", nickname = "exportProjectsWithoutInvoices")
    public ResponseEntity<byte[]> exportProjectsWithoutInvoices(@RequestParam(defaultValue = "1") int report,
                                                                @RequestParam(defaultValue = "false") Boolean orderCanceled,
                                                                @RequestParam(defaultValue = "30") int percentage,
                                                                @RequestParam(required = false) List<String> pKeys) {
        log.info("Exporting projects without invoices");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "report.xlsx");
        byte[] arrayOuput = projectInvoiceService.exportProjectsWithoutInvoices(report, orderCanceled, percentage, pKeys);
        return ResponseEntity.ok().headers(headers).body(arrayOuput);
    }

    @GetMapping("notification")
    @ApiOperation(httpMethod = "GET", value = "Servicio para enviar correo de proyectos sin facturas", nickname = "sendNotificationProjectsWithoutInvoices")
    public ResponseEntity<?> sendNotificationProjectsWithoutInvoices(@RequestParam(defaultValue = "1") int report,
                                                                     @RequestParam(defaultValue = "false") Boolean orderCanceled,
                                                                     @RequestParam(defaultValue = "30") int percentage,
                                                                     @RequestParam(required = false) List<String> pKeys) {
        log.info("Sending notificaton email projects without invoices");
        projectInvoiceService.sendNotificationProjectsWithoutInvoices(report, orderCanceled, percentage, pKeys);
        return ResponseEntity.ok().build();
    }


}
