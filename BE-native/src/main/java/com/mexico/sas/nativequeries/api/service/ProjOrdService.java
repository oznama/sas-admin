package com.mexico.sas.nativequeries.api.service;

import com.mexico.sas.nativequeries.api.mail.EmailUtils;
import com.mexico.sas.nativequeries.api.model.ProjectWithoutOrders;
import com.mexico.sas.nativequeries.api.report.ProjectWithoutODCXls;
import com.mexico.sas.nativequeries.api.repository.ProjOrdRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ProjOrdService {

    @Autowired
    private ProjOrdRepository projOrdRepository;

    @Autowired
    private ProjectWithoutODCXls projectWithoutODCXls;

    @Autowired
    private EmailUtils emailUtils;

    public Page<ProjectWithoutOrders> findProjectsWithoutOrders(String filter, Long paStatus, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        log.debug("Finding Projects without orders pagged {}", pageable);
        return projOrdRepository.findProjectsWithoutOrders(filter, paStatus, pageable);
    }

    public List<ProjectWithoutOrders> findProjectsWithoutOrders(String filter, Long paStatus) {
        return projOrdRepository.findProjectsWithoutOrders(filter, paStatus);
    }

    public byte[] exportProjectsWithoutOrders(List<String> pKeys) {
        return projectWithoutODCXls.build(projOrdRepository.findProjectsWithoutOrders(pKeys));
    }

    // TODO Add Async
    public void sendNotificationProjectsWithoutOrders(Boolean sendBoss, String bossEmail, List<String> pKeys) {
        final String htlmTemplate = "pending_orders";
        List<ProjectWithoutOrders> projects = projOrdRepository.findProjectsWithoutOrders(pKeys);
        projects.forEach( p -> {
            log.debug("Sending email notification Project {} - {} without order to PM: {}, boss: {}, and ownBoss: {}",
                    p.getProjectKey(), p.getProjectName(), p.getPmMail(),
                    (sendBoss && p.getBossMail() != null ? p.getBossMail() : null), bossEmail);
            String subject = String.format("%s %s orden de compra pendiente - SAS", p.getProjectKey(), p.getProjectName());
            Map<String, Object> variables = new HashMap<>();
            variables.put("pKey", p.getProjectKey());
            variables.put("pName", p.getProjectName());
            variables.put("pmName", p.getPmName());
            emailUtils.sendMessage("oznama27@gmail.com", subject, htlmTemplate, variables,
                    sendBoss && p.getBossMail() != null ? p.getBossMail() : null,
                    bossEmail);
                }
        );
    }
}
