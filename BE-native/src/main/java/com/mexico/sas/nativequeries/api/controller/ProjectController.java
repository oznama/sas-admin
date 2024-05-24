package com.mexico.sas.nativequeries.api.controller;

import com.mexico.sas.nativequeries.api.model.ProjectWithoutOrders;
import com.mexico.sas.nativequeries.api.service.ProjOrdService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("projects")
public class ProjectController {

    @Autowired
    private ProjOrdService projOrdService;

    @GetMapping("withoutorders")
    @ApiOperation(httpMethod = "GET", value = "Servicio para recuperar proyectos sin ordenes", nickname = "findProjectsWithoutOrders")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = ProjectWithoutOrders.class, responseContainer = "List") })
    public ResponseEntity<List<ProjectWithoutOrders>> findProjectsWithoutOrders(@RequestParam(required = false) String filter,
                                                                                @RequestParam(required = false) Long paStatus) {
        log.info("Finding projects without orders");
        return ResponseEntity.ok(projOrdService.findProjectsWithoutOrders(filter, paStatus));
    }

    @GetMapping("withoutorders/export")
    @ApiOperation(httpMethod = "GET", value = "Servicio para exportar proyectos sin ordenes", nickname = "exportProjectsWithoutOrders")
    public ResponseEntity<byte[]> exportProjectsWithoutOrders(@RequestParam(required = false) List<String> pKeys) {
        log.info("Exporting projects without orders");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "report.xlsx");
        byte[] arrayOuput = projOrdService.exportProjectsWithoutOrders(pKeys);
        return ResponseEntity.ok().headers(headers).body(arrayOuput);
    }


}
