package com.mexico.sas.nativequeries.api.controller;

import com.mexico.sas.nativequeries.api.model.ProjectWithApplication;
import com.mexico.sas.nativequeries.api.service.ProjectOrderService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("projects/withoutorders")
public class ProjectOrderController {

    @Autowired
    private ProjectOrderService projectOrderService;

    @GetMapping
    @ApiOperation(httpMethod = "GET", value = "Servicio para recuperar proyectos sin ordenes", nickname = "findProjectsWithoutOrders")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = ProjectWithApplication.class, responseContainer = "List") })
    public ResponseEntity<Page<ProjectWithApplication>> findProjectsWithoutOrders(@RequestParam(required = false) String filter,
                                                                                  @RequestParam(defaultValue = "0") int page,
                                                                                  @RequestParam(defaultValue = "10") int size) {
        log.info("Finding projects without orders");
        return ResponseEntity.ok(projectOrderService.findProjectsWithoutOrders(filter, page, size));
    }

    @GetMapping("export")
    @ApiOperation(httpMethod = "GET", value = "Servicio para exportar proyectos sin ordenes", nickname = "exportProjectsWithoutOrders")
    public ResponseEntity<byte[]> exportProjectsWithoutOrders(@RequestParam(required = false) List<String> pKeys) {
        log.info("Exporting projects without orders");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "report.xlsx");
        byte[] arrayOuput = projectOrderService.exportProjectsWithoutOrders(pKeys);
        return ResponseEntity.ok().headers(headers).body(arrayOuput);
    }

    @GetMapping("notification")
    @ApiOperation(httpMethod = "GET", value = "Servicio para enviar correo de proyectos sin ordenes", nickname = "sendProjectsWithoutOrders")
    public ResponseEntity<?> sendProjectsWithoutOrders(@RequestParam(required = false) List<String> pKeys) {
        log.info("Sending notificaton email projects without orders");
        projectOrderService.sendNotificationProjectsWithoutOrders(pKeys);
        return ResponseEntity.ok().build();
    }


}
