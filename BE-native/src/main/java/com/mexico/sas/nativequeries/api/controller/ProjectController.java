package com.mexico.sas.nativequeries.api.controller;

import com.mexico.sas.nativequeries.api.model.ProjectPlan;
import com.mexico.sas.nativequeries.api.model.ProjectWithApplication;
import com.mexico.sas.nativequeries.api.service.ProjectService;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("projects")
public class ProjectController {

    @Autowired
    private ProjectService projectOrder;

    @GetMapping
    @ApiOperation(httpMethod = "GET", value = "Servicio para recuperar proyectos con aplicaciones", nickname = "findProjectsWithApplications")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = ProjectWithApplication.class, responseContainer = "List") })
    public ResponseEntity<Page<ProjectWithApplication>> findProjectsWithApplications(@RequestParam(required = false) String filter,
                                                                                     @RequestParam(defaultValue = "false") Boolean installation,
                                                                                     @RequestParam(defaultValue = "false") Boolean monitoring,
                                                                                     @RequestParam(defaultValue = "0") int page,
                                                                                     @RequestParam(defaultValue = "10") int size) {
        log.info("Finding projects with applications");
        return ResponseEntity.ok(projectOrder.findProjectsWithApplication(filter, installation, monitoring, page, size));
    }

    @GetMapping("keys")
    @ApiOperation(httpMethod = "GET", value = "Servicio para recuperar claves de proyectos con aplicaciones", nickname = "findProjectsKeysWithApplications")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = String.class, responseContainer = "List") })
    public ResponseEntity<List<String>> findProjectsKeysWithApplications(@RequestParam(required = false) String filter,
                                                                                     @RequestParam(defaultValue = "false") Boolean installation,
                                                                                     @RequestParam(defaultValue = "false") Boolean monitoring) {
        log.info("Finding projects keys with applications");
        return ResponseEntity.ok(projectOrder.findProjectsWithApplication(filter, installation, monitoring));
    }

    @GetMapping("export")
    @ApiOperation(httpMethod = "GET", value = "Servicio para exportar proyectos con aplicaciones", nickname = "exportProjectsWithApplications")
    public ResponseEntity<byte[]> exportProjectsWithApplications(@RequestParam(defaultValue = "false") Boolean installation,
                                                                 @RequestParam(defaultValue = "false") Boolean monitoring,
                                                                 @RequestParam(required = false) List<String> pKeys) {
        log.info("Exporting projects with applications");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "report.xlsx");
        byte[] arrayOuput = projectOrder.exportProjectsWithApplication(installation, monitoring, pKeys);
        return ResponseEntity.ok().headers(headers).body(arrayOuput);
    }

    @GetMapping("notification")
    @ApiOperation(httpMethod = "GET", value = "Servicio para enviar correo de proyectos con aplicaciones", nickname = "sendProjectsWithApplications")
    public ResponseEntity<?> sendProjectsWithApplications(@RequestParam(defaultValue = "false") Boolean installation,
                                                          @RequestParam(defaultValue = "false") Boolean monitoring,
                                                          @RequestParam(required = false) List<String> pKeys) {
        log.info("Sending notificaton email projects with applications");
        projectOrder.sendNotificationProjectsWithApplication(installation, monitoring, pKeys);
        return ResponseEntity.ok().build();
    }


    @GetMapping("check/project-plan")
    @ApiOperation(httpMethod = "GET", value = "Servicio para checar las aplicaciones de plan de trabajo", nickname = "checkProjectPlan")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = ProjectPlan.class, responseContainer = "List") })
    public ResponseEntity<List<ProjectPlan>> checkProjectPlan(@RequestParam String pKey ) {
        log.info("Checking project plan aplications status");
        return ResponseEntity.ok(projectOrder.checkProjectPlan(pKey));
    }

    @PostMapping("send/project-plan")
    @ApiOperation(httpMethod = "POST", value = "Servicio para enviar correo de plan de trabajo", nickname = "sendProjectsWithApplications")
    public ResponseEntity<?> sendProjectPlan(@RequestParam String username,
                                             @RequestParam String password,
                                             @RequestParam String pKey,
                                             @RequestParam String apps,
                                             @RequestParam("file") MultipartFile file) throws IOException {
        log.info("Sending project plan with file {}", file.getOriginalFilename());
        projectOrder.sendProjectPlan(username, password, pKey, apps, file.getOriginalFilename(), file.getInputStream());
        return ResponseEntity.ok().build();
    }


}
