package com.mexico.sas.nativequeries.api.service;

import com.mexico.sas.nativequeries.api.mail.EmailUtils;
import com.mexico.sas.nativequeries.api.model.ProjectWithoutOrders;
import com.mexico.sas.nativequeries.api.report.ProjectWithoutODCXls;
import com.mexico.sas.nativequeries.api.repository.ProjectOrderRepository;
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
public class ProjectOrderService {

    @Autowired
    private ProjectOrderRepository projectOrderRepository;

    @Autowired
    private ProjectWithoutODCXls projectWithoutODCXls;

    @Autowired
    private EmailUtils emailUtils;

    public Page<ProjectWithoutOrders> findProjectsWithoutOrders(String filter, Long paStatus, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        log.debug("Finding Projects without orders pagged {}", pageable);
        return projectOrderRepository.findProjectsWithoutOrders(filter, paStatus, pageable);
    }

    public List<ProjectWithoutOrders> findProjectsWithoutOrders(String filter, Long paStatus) {
        return projectOrderRepository.findProjectsWithoutOrders(filter, paStatus);
    }

    public byte[] exportProjectsWithoutOrders(List<String> pKeys) {
        return projectWithoutODCXls.build(projectOrderRepository.findProjectsWithoutOrders(pKeys));
    }

    public void sendNotificationProjectsWithoutOrders(String currentUserEmail, String bossEmail, List<String> pKeys) {
        final String htlmTemplate = "pending_orders";
        List<ProjectWithoutOrders> projects = projectOrderRepository.findProjectsWithoutOrders(pKeys);
        projects.forEach( p -> {
            log.debug("Sending email notification Project {} - {} without order", p.getProjectKey(), p.getProjectName());
            String subject = String.format("%s %s orden de compra pendiente - SAS", p.getProjectKey(), p.getProjectName());
            Map<String, Object> variables = new HashMap<>();
            variables.put("pmName", p.getPmName());
            emailUtils.sendMessage("danterencon@gmail.com", subject, htlmTemplate, variables,
                    currentUserEmail, bossEmail/*, p.getPmMail(), p.getBossMail()*/);
        });
    }
}
