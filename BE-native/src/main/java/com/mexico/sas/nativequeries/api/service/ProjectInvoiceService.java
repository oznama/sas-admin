package com.mexico.sas.nativequeries.api.service;

import com.mexico.sas.nativequeries.api.mail.EmailUtils;
import com.mexico.sas.nativequeries.api.model.ProjectWithoutInvoices;
import com.mexico.sas.nativequeries.api.report.ProjectWithoutInvXls;
import com.mexico.sas.nativequeries.api.repository.ProjectInvoiceRepository;
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
public class ProjectInvoiceService {

    @Autowired
    private ProjectInvoiceRepository projectInvoiceRepository;

    @Autowired
    private ProjectWithoutInvXls projectWithoutInvXls;

    @Autowired
    private EmailUtils emailUtils;

    public Page<ProjectWithoutInvoices> findProjectsWithoutInvoices(String filter, Long report, Long paStatus, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        log.debug("Finding Projects without invoices pagged {}", pageable);
        return projectInvoiceRepository.findProjectsWithoutInvoices(filter, report, paStatus, pageable);
    }

    public byte[] exportProjectsWithoutInvoices(List<String> pKeys) {
        return projectWithoutInvXls.build(projectInvoiceRepository.findProjectsWithoutInvoices(pKeys));
    }

    @Async("ExecutorAsync")
    public void sendNotificationProjectsWithoutInvoices(List<String> pKeys) {
        final String htlmTemplate = "pending_invoices";
        List<ProjectWithoutInvoices> projects = projectInvoiceRepository.findProjectsWithoutInvoices(pKeys);
        projects.forEach( p -> {
            log.debug("Sending email notification Project {} - {} without invoice", p.getProjectKey(), p.getProjectName());
            String subject = String.format("%s %s factura  pendiente - SAS", p.getProjectKey(), p.getProjectName());
            Map<String, Object> variables = new HashMap<>();
            variables.put("pKey", p.getProjectKey());
            variables.put("pName", p.getProjectName());
            variables.put("pmName", p.getPmName());
            emailUtils.sendMessage(p.getPmMail(), subject, htlmTemplate, variables, p.getBossMail());
        });
    }
}
