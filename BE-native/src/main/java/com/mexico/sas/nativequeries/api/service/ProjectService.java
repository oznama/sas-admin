package com.mexico.sas.nativequeries.api.service;

import com.mexico.sas.nativequeries.api.mail.EmailUtils;
import com.mexico.sas.nativequeries.api.model.ProjectPlan;
import com.mexico.sas.nativequeries.api.model.ProjectPlanDetail;
import com.mexico.sas.nativequeries.api.model.ProjectPlanHeader;
import com.mexico.sas.nativequeries.api.model.ProjectWithApplication;
import com.mexico.sas.nativequeries.api.report.ProjectWithApplicationXls;
import com.mexico.sas.nativequeries.api.repository.ProjectPlanRepository;
import com.mexico.sas.nativequeries.api.repository.ProjectRepository;
import com.mexico.sas.nativequeries.api.repository.SQLConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.InputStream;
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
    private ProjectPlanRepository projectPlanRepository;

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

    public List<ProjectPlan> checkProjectPlan(String pKey) {
        log.debug("Checking applications of project {}", pKey);
        return projectPlanRepository.getProjectPlanApps(pKey);
    }

    public boolean sendProjectPlan(String username, String password, String pKey, String apps, String fileName, InputStream inputStream) {
        try {
            final String htlmTemplate = "project_plan";
            log.debug("Seding project plan of {}", pKey);
            ProjectPlanHeader projectPlanHeader = projectPlanRepository.getProjectPlanHeader(pKey);
            List<ProjectPlanDetail> projectPlanDetail = projectPlanRepository.getProjectPlanDetail(pKey, List.of(apps.split(SQLConstants.COMMA)));
            if( projectPlanDetail.isEmpty() ) {
                log.error("Applications {} in project {} not found", apps, pKey);
                return false;
            }
            String subject = String.format("%s - %s de plan con fechas", projectPlanHeader.getPKey(), projectPlanHeader.getPDescription());
            Map<String, Object> variables = new HashMap<>();
            variables.put("pmName", projectPlanHeader.getPmName());
            variables.put("detail", projectPlanDetail);
            List<String> cc = emailUtils.getListCc(null);
            projectPlanDetail.forEach( d -> {
                cc.add(d.getLeaderMail());
                cc.add(d.getDeveloperMail());
            });
            emailUtils.sendMessage(username, password, projectPlanHeader.getPmMail(), subject, htlmTemplate, variables, fileName, inputStream, cc.toArray(new String[0]));
            projectPlanRepository.updateDate(pKey);
            return true;
        } catch (Exception e) {
            log.error("Error to send project plan", e);
            return false;
        }
    }
}
