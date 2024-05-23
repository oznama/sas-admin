package com.mexico.sas.nativequeries.api.controller;

import com.mexico.sas.nativequeries.api.model.ProjectWithoutOrders;
import com.mexico.sas.nativequeries.api.repository.ProjOrdRepository;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("projects")
public class ProjectController {

    @Autowired
    private ProjOrdRepository projOrdRepository;

    @GetMapping("withoutorders")
    @ApiOperation(httpMethod = "GET", value = "Servicio para recuperar proyectos sin ordenes", nickname = "findProjectsWithoutOrders")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = ProjectWithoutOrders.class, responseContainer = "List") })
    public ResponseEntity<List<ProjectWithoutOrders>> findProjectsWithoutOrders(@RequestParam(required = false) String filter,
                                                                                @RequestParam(required = false) Long paStatus) {
        log.info("Finding projects without orders");
        return ResponseEntity.ok(projOrdRepository.findProjectsWithoutOrders(filter, paStatus));
    }

}
