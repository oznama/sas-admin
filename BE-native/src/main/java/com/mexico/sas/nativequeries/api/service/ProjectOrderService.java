package com.mexico.sas.nativequeries.api.service;

import com.mexico.sas.nativequeries.api.mail.EmailUtils;
import com.mexico.sas.nativequeries.api.model.ProjectWithApplication;
import com.mexico.sas.nativequeries.api.report.ProjectWithApplicationXls;
import com.mexico.sas.nativequeries.api.repository.ProjectOrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class ProjectOrderService {

    @Autowired
    private ProjectOrderRepository projectOrderRepository;

    @Autowired
    private ProjectWithApplicationXls projectWithApplicationXls;

    @Autowired
    private EmailUtils emailUtils;

    public Page<ProjectWithApplication> findProjectsWithoutOrders(String filter, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        log.debug("Finding Projects without orders pagged {}", pageable);
        return projectOrderRepository.findProjectsWithoutOrders(filter, pageable);
    }

    public List<String> findProjectsWithoutOrders(String filter) {
        log.debug("Finding Projects keys without orders");
        return projectOrderRepository.findProjectsWithoutOrders(filter);
    }

    public byte[] exportProjectsWithoutOrders(List<String> pKeys) {
        return projectWithApplicationXls.build("Proyectos sin ordenes de compra", projectOrderRepository.findProjectsWithoutOrders(pKeys));
    }

    @Async("ExecutorAsync")
    public void sendNotificationProjectsWithoutOrders(List<String> pKeys) {
        final String htlmTemplate = "pending_orders";
        List<ProjectWithApplication> projects = projectOrderRepository.findProjectsWithoutOrders(pKeys);
        projects.forEach( p -> {
            log.debug("Sending email notification Project {} - {} without order", p.getProjectKey(), p.getProjectName());
            String subject = String.format("%s %s orden de compra pendiente - SAS", p.getProjectKey(), p.getProjectName());
            Map<String, Object> variables = new HashMap<>();
            variables.put("pmName", p.getPmName());
            emailUtils.sendMessage(p.getPmMail(), subject, htlmTemplate, variables, p.getBossMail());
        });
    }
}
