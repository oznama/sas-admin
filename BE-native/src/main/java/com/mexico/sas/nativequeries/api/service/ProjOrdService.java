package com.mexico.sas.nativequeries.api.service;

import com.mexico.sas.nativequeries.api.model.ProjectWithoutOrders;
import com.mexico.sas.nativequeries.api.report.ExcelExporter;
import com.mexico.sas.nativequeries.api.repository.ProjOrdRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Slf4j
public class ProjOrdService {

    @Autowired
    private ProjOrdRepository projOrdRepository;

    @Autowired
    private ExcelExporter excelExporter;

    public List<ProjectWithoutOrders> findProjectsWithoutOrders(String filter, Long paStatus) {
        return projOrdRepository.findProjectsWithoutOrders(filter, paStatus);
    }

    public byte[] exportProjectsWithoutOrders(List<String> pKeys) {
        return excelExporter.build(projOrdRepository.findProjectsWithoutOrders(pKeys));
    }

    // TODO Add Async
    public void sendNotificationProjectsWithoutOrders(Boolean sendBoss, String bossEmail, List<String> pKeys) {
        List<ProjectWithoutOrders> projects = projOrdRepository.findProjectsWithoutOrders(pKeys);
        projects.forEach( p ->
                log.debug("Sending email notification Project {} without order to PM: {} - {}, boss: {}, and ownBoss: {}",
                        p.getProjectKey(), p.getProjectName(), p.getPmMail(),
                        (sendBoss && p.getBossMail() != null ? p.getBossMail() : null), bossEmail)
        );
    }
}
