package com.mexico.sas.nativequeries.api.service;

import com.mexico.sas.nativequeries.api.model.ProjectWithoutOrders;
import com.mexico.sas.nativequeries.api.report.ExcelExporter;
import com.mexico.sas.nativequeries.api.repository.ProjOrdRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
