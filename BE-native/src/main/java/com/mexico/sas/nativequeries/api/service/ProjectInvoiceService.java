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

    public Page<ProjectWithoutInvoices> findProjectsWithoutInvoices(String filter, Integer report, Boolean orderCanceled, Integer percentage, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        log.debug("Finding Projects without invoices pagged {}", pageable);
        if( report >= 1 && report <= 3 )
            return projectInvoiceRepository.findProjectsWithoutInvoices(filter, report, orderCanceled, percentage, pageable);
        else {
            log.warn("Report {} not supported", report);
            return Page.empty();
        }
    }

    public byte[] exportProjectsWithoutInvoices(Integer report, Boolean orderCanceled, Integer percentage, List<String> pKeys) {
        return projectWithoutInvXls.build(projectInvoiceRepository.findProjectsWithoutInvoices(report, orderCanceled, percentage, pKeys));
    }

    @Async("ExecutorAsync")
    public void sendNotificationProjectsWithoutInvoices(Integer report, Boolean orderCanceled, Integer percentage, List<String> pKeys) {
        final String htlmTemplate = "pending_invoices";
        List<ProjectWithoutInvoices> projects = projectInvoiceRepository.findProjectsWithoutInvoices(report, orderCanceled, percentage, pKeys);
        projects.forEach( p -> {
            log.debug("Sending email notification Project {} - {} without invoice", p.getProjectKey(), p.getProjectName());
            String subject = String.format("%s %s factura  pendiente - SAS", p.getProjectKey(), p.getProjectName());
            Map<String, Object> variables = new HashMap<>();
            variables.put("pName", p.getProjectName());
            emailUtils.sendMessage(p.getPmMail(), subject, htlmTemplate, variables, p.getBossMail());
        });
    }
}
