package com.mexico.sas.nativequeries.api.service;

import com.mexico.sas.nativequeries.api.crypt.Crypter;
import com.mexico.sas.nativequeries.api.mail.EmailUtils;
import com.mexico.sas.nativequeries.api.model.*;
import com.mexico.sas.nativequeries.api.repository.ProjectPlanRepository;
import com.mexico.sas.nativequeries.api.repository.SQLConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.util.*;


@Service
@Slf4j
public class ProjectPlanService {

    @Autowired
    private ProjectPlanRepository projectPlanRepository;

    @Autowired
    private EmailUtils emailUtils;

    @Autowired
    private Crypter crypter;

    public List<String> checkProjectPlan(String pKey) {
        log.debug("Checking applications for project plan {}", pKey);
        return projectPlanRepository.getProjectPlanApps(pKey);
    }

    public List<ProjectPlanDetail> getProjectPlanDetail(String pKey, String apps) {
        log.debug("Getting preview applications of project {}", pKey);
        return projectPlanRepository.getProjectPlanDetail(pKey, Arrays.asList(apps.split(SQLConstants.COMMA)));
    }

    public boolean sendProjectPlan(ProjectPlanData projectPlanData) {
        try {
            log.debug("Sending project plan of {}", projectPlanData.getPKey());
            ProjectPlanHeader projectPlanHeader = projectPlanRepository.getProjectPlanHeader(projectPlanData.getPKey());
            List<ProjectPlanDetail> projectPlanDetail = projectPlanRepository
                    .getProjectPlanDetail(projectPlanData.getPKey(), Arrays.asList(projectPlanData.getApps().split(SQLConstants.COMMA)));
            if( projectPlanDetail.isEmpty() ) {
                log.error("Applications {} in project {} not found", projectPlanData.getPKey(), projectPlanData.getApps());
                return false;
            }
            processProjectPlanEmail(projectPlanData, projectPlanHeader, projectPlanDetail);
            projectPlanRepository.updateDate(projectPlanHeader.getPKey());
            return true;
        } catch (Exception e) {
            log.error("Error to send project plan", e);
            return false;
        }
    }

    private void processProjectPlanEmail(ProjectPlanData projectPlanData, ProjectPlanHeader projectPlanHeader, List<ProjectPlanDetail> projectPlanDetail) throws Exception {
        final String htlmTemplate = "project_plan";
        String subject = String.format("%s - %s de plan con fechas", projectPlanHeader.getPKey(), projectPlanHeader.getPDescription());
        Map<String, Object> variables = new HashMap<>();
        variables.put("pmName", projectPlanHeader.getPmName());
        variables.put("detail", projectPlanDetail);
        List<String> cc = emailUtils.getListCc(null);
        projectPlanDetail.forEach(d -> {
            cc.add(d.getLeaderMail());
            cc.add(d.getDeveloperMail());
        });
        List<String> ccWithoutDuplicates = new ArrayList<>(new HashSet<>(cc));
        emailUtils.sendMessage(projectPlanData.getUsername(), crypter.decrypt(projectPlanData.getPassword()),
                projectPlanHeader.getPmMail(), subject, htlmTemplate, variables,
                !StringUtils.isEmpty(projectPlanData.getFileName()) ? projectPlanData.getFileName() : "",
                projectPlanData.getFile() != null ? projectPlanData.getFile().getInputStream() : null,
                ccWithoutDuplicates.toArray(new String[0]));
    }
}
