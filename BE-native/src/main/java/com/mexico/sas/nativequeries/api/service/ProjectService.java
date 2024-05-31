package com.mexico.sas.nativequeries.api.service;

import com.mexico.sas.nativequeries.api.mail.EmailUtils;
import com.mexico.sas.nativequeries.api.model.ProjectWithApplication;
import com.mexico.sas.nativequeries.api.report.ProjectWithApplicationXls;
import com.mexico.sas.nativequeries.api.repository.ProjectRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectWithApplicationXls projectWithApplicationXls;

    @Autowired
    private EmailUtils emailUtils;

    public Page<ProjectWithApplication> findProjectsWithApplication(String filter, Boolean installed, Boolean monitoring, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        log.debug("Finding Projects with applications pagged {}", pageable);
        return projectRepository.findProjectsWithApplication(filter, installed, monitoring, pageable);
    }

    public List<String> findProjectsWithApplication(String filter, Boolean installed, Boolean monitoring) {
        log.debug("Finding Projects keys with applications");
        return projectRepository.findProjectsWithApplication(filter, installed, monitoring);
    }

    public byte[] exportProjectsWithApplication(Boolean installed, Boolean monitoring, List<String> pKeys) {
        return projectWithApplicationXls.build("Proyectos sin fecha", projectRepository.findProjectsWithApplication(installed, monitoring, pKeys));
    }

    @Async("ExecutorAsync")
    public void sendNotificationProjectsWithApplication(Boolean installed, Boolean monitoring, List<String> pKeys) {
        final String htlmTemplate = "pending_dates";
        List<ProjectWithApplication> projects = projectRepository.findProjectsWithApplication(installed, monitoring, pKeys);
        projects.forEach( p -> {
            log.debug("Sending email notification Project {} - {} pendings", p.getProjectKey(), p.getProjectName());
            String subject = String.format("%s %s pendientes - SAS", p.getProjectKey(), p.getProjectName());
            Map<String, Object> variables = new HashMap<>();
            variables.put("pmName", p.getPmName());
            StringBuilder message = new StringBuilder();
            if(StringUtils.isEmpty(p.getInstallationDate()))
                message.append("fecha de instalación");
            if(StringUtils.isEmpty(p.getMonitoringDate()))
                message.append("fecha de monitoreo");
            variables.put("message", message);
            emailUtils.sendMessage(p.getPmMail(), subject, htlmTemplate, variables, p.getBossMail());
        });
    }
}
