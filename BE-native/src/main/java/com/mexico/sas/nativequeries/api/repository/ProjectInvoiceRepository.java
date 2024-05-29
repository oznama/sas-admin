package com.mexico.sas.nativequeries.api.repository;

import com.mexico.sas.nativequeries.api.model.ProjectWithoutInvoices;
import com.mexico.sas.nativequeries.api.model.ProjectWithoutOrders;
import com.mexico.sas.nativequeries.api.model.mapper.ProjectWihtoutInvoicesMapper;
import com.mexico.sas.nativequeries.api.model.mapper.ProjectWihtoutOrdersMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class ProjectInvoiceRepository extends BaseRepository {

    @Value("${query.project.without.invoice}")
    private String queryProjectWithoutInvoice;

    @Value("${query.project.application.developed.complete}")
    private String filterApplicationDevelopStatus;

    @Value("${query.project.application.instalation.expired}")
    private String filterApplicationInstalation;

    public Page<ProjectWithoutInvoices> findProjectsWithoutInvoices(String filter, Long report, Long paStatus, Pageable pageable) {
        log.debug("findProjectsWithoutInvoice Pagged...");
        List<ProjectWithoutInvoices> list = new ArrayList<>();
        List<String> conditions = projectsWithoutInvoiceFilter(filter, report, paStatus, null);
        String query = queryProjectWithoutInvoice
                .replace(SQLConstants.WHERE_CLAUSE_PARAMETER, !conditions.isEmpty() ? whereClauseBuilder(conditions) : "");
        Long total = queryForObject(queryCount(query), Long.class);
        log.debug("{} row found!", total);
        if( total > 0 ) {
            query = queryPagged(query, pageable.getPageSize(), pageable.getPageNumber() * pageable.getPageSize());
            list = query(query, new ProjectWihtoutInvoicesMapper());
        }
        return new PageImpl<>(list, pageable, total);
    }

    public List<ProjectWithoutInvoices> findProjectsWithoutInvoices(List<String> pKeys) {
        log.debug("findProjectsWithoutInvoce with pKeys...");
        return execute(projectsWithoutInvoiceFilter( null, null, null, pKeys));
    }

    private List<ProjectWithoutInvoices> execute(List<String> conditions) {
        // Si hay filtros, se agregan al query si no, no queda vacio
        String query = queryProjectWithoutInvoice
                .replace(SQLConstants.WHERE_CLAUSE_PARAMETER, !conditions.isEmpty() ? whereClauseBuilder(conditions) : "");
        // Executa el query y lo mapea en el objeto ProjectWihtoutOrdersMapper
        return query(query, new ProjectWihtoutInvoicesMapper());
    }

    private List<String> projectsWithoutInvoiceFilter(String filter, Long report, Long paStatus, List<String> pKeys) {
        log.debug("Checking filters, filter: {}, paStatus: {}", filter, paStatus);
        List<String> conditions = new ArrayList<>();

        addGeneralFilter(filter, conditions);

        // Si es reporte construccion y hay valor en filtro paStatus
        if( report != null && report == 1 && paStatus != null ) {
            String paStatusCondition = filterApplicationDevelopStatus
                    .replaceAll(SQLConstants.PROJECT_APP_STATUS_PARAMETER, String.valueOf(paStatus));
            conditions.add(paStatusCondition);
        }
        // Si es reporte instalacion y hay valor en filtro paStatus
        else if( report != null && report == 2 && paStatus != null ) {
            String paStatusCondition = filterApplicationInstalation
                    .replaceAll(SQLConstants.PROJECT_APP_STATUS_PARAMETER, String.valueOf(paStatus));
            conditions.add(paStatusCondition);
        }

        addPKeysIn(pKeys, conditions);

        log.debug("Conditions generated? {}", conditions.size());
        return conditions;
    }
}
