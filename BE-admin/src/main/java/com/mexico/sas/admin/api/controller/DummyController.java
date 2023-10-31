package com.mexico.sas.admin.api.controller;

import com.mexico.sas.admin.api.dummy.ProjectDummy;
import com.mexico.sas.admin.api.dummy.ProjectDummyRepository;
import com.mexico.sas.admin.api.exception.CustomException;
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
@RequestMapping("/dummy")
public class DummyController {

    @Autowired
    private ProjectDummyRepository projectDummyRepository;

    @GetMapping
    @ApiOperation(httpMethod = "GET", value = "Servicio para todos los proyectos dummies", nickname = "/findAll")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = ProjectDummy.class, responseContainer = "List") })
    public ResponseEntity<List<ProjectDummy>> findAll() throws CustomException {
        log.info("Finding projects dummies");
        return ResponseEntity.ok(projectDummyRepository.findAll());
    }

    @GetMapping("/{id}")
    @ApiOperation(httpMethod = "GET", value = "Servicio para recuperar proyecto dummy por id", nickname = "findById")
    public ResponseEntity<ProjectDummy> findById(@PathVariable("id") Long id) throws CustomException {
        log.info("Finding project dummy by id");
        return ResponseEntity.ok(projectDummyRepository.findById(id));
    }

    @PostMapping(headers = "Accept=application/json")
    @ResponseStatus(code = HttpStatus.CREATED)
    @ApiOperation(httpMethod = "POST", value = "Servicio para crear proyecto dummy", nickname = "save")
    public ResponseEntity<ProjectDummy> save(@Valid @RequestBody ProjectDummy projectDummy) throws CustomException {
        log.info("Saving project dummy");
        return ResponseEntity.status(HttpStatus.CREATED).body(projectDummyRepository.save(projectDummy));
    }
}
