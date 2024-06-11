package com.mexico.sas.nativequeries.api.controller;

import com.mexico.sas.nativequeries.api.model.ProjectPlanData;
import com.mexico.sas.nativequeries.api.model.ProjectPlanDetail;
import com.mexico.sas.nativequeries.api.service.ProjectPlanService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("projects/project-plan")
public class ProjectPlanController {

    @Autowired
    private ProjectPlanService projectPlanService;

    @GetMapping("check")
    @ApiOperation(httpMethod = "GET", value = "Servicio para checar las aplicaciones de plan de trabajo", nickname = "checkProjectPlan")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = String.class, responseContainer = "List") })
    public ResponseEntity<List<String>> checkProjectPlan(@RequestParam String pKey) {
        log.info("Checking project plan aplications status");
        return ResponseEntity.ok(projectPlanService.checkProjectPlan(pKey));
    }

    @GetMapping("preview")
    @ApiOperation(httpMethod = "GET", value = "Servicio para recuperar vista previa del correo de plan de trabajo", nickname = "getPreviewProjectPlan")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = ProjectPlanDetail.class, responseContainer = "List") })
    public ResponseEntity<List<ProjectPlanDetail>> getPreviewProjectPlan(@RequestParam String pKey, @RequestParam String apps) {
        log.info("Getting project plan email preview");
        return ResponseEntity.ok(projectPlanService.getProjectPlanDetail(pKey, apps));
    }

    @PostMapping(path = "send", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiOperation(httpMethod = "POST", value = "Servicio para enviar correo de plan de trabajo", nickname = "sendProjectsWithApplications")
    public ResponseEntity<?> sendProjectPlan(@ModelAttribute ProjectPlanData projectPlanData) {
        log.info("Sending project plan with file {}", projectPlanData.getFileName());
        if( projectPlanService.sendProjectPlan(projectPlanData) )
            return ResponseEntity.ok().build();
        else
            return ResponseEntity.badRequest().build();
    }
}
