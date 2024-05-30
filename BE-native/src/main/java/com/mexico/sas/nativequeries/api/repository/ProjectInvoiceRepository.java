package com.mexico.sas.nativequeries.api.repository;

import com.mexico.sas.nativequeries.api.model.ProjectWithoutInvoices;
import com.mexico.sas.nativequeries.api.model.mapper.ProjectWihtoutInvoicesMapper;
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

    @Value("${query.project.application.invoice.developed}")
    private String filterApplicationInvoceDeveloped;

    @Value("${query.project.order.status.canceled}")
    private String filterOrderCanceled;

    @Value("${query.project.order.status.not.canceled}")
    private String filterOrderNotCanceled;

    @Value("${query.project.invoice.installed}")
    private String filterProjectInvoiceInstalled;

    @Value("${query.project.invoice.monitoring}")
    private String filterProjectInvoiceMonitoring;

    public Page<ProjectWithoutInvoices> findProjectsWithoutInvoices(String filter, Integer report, Boolean orderCanceled, Integer percentage, Pageable pageable) {
        log.debug("findProjectsWithoutInvoice Pagged...");
        List<ProjectWithoutInvoices> list = new ArrayList<>();
        List<String> conditions = projectsWithoutInvoiceFilter(filter, report, orderCanceled, null);
        String query = queryProjectWithoutInvoice
                .replace(SQLConstants.WHERE_CLAUSE_PARAMETER, !conditions.isEmpty() ? whereClauseBuilder(conditions) : "")
                .replace(SQLConstants.PERCENTAGE_PARAMETER, String.valueOf(percentage));
        Long total = queryForObject(queryCount(query), Long.class);
        log.debug("{} row found!", total);
        if( total > 0 ) {
            query = queryPagged(query, pageable.getPageSize(), pageable.getPageNumber() * pageable.getPageSize());
            list = query(query, new ProjectWihtoutInvoicesMapper());
        }
        return new PageImpl<>(list, pageable, total);
    }

    public List<ProjectWithoutInvoices> findProjectsWithoutInvoices(Integer report, Boolean orderCanceled, Integer percentage, List<String> pKeys) {
        log.debug("findProjectsWithoutInvoce with pKeys...");
        return execute(percentage, projectsWithoutInvoiceFilter( null, report, orderCanceled, pKeys));
    }

    private List<ProjectWithoutInvoices> execute(Integer percentage, List<String> conditions) {
        // Si hay filtros, se agregan al query si no, no queda vacio
        String query = queryProjectWithoutInvoice
                .replace(SQLConstants.WHERE_CLAUSE_PARAMETER, !conditions.isEmpty() ? whereClauseBuilder(conditions) : "")
                .replace(SQLConstants.PERCENTAGE_PARAMETER, String.valueOf(percentage));
        // Executa el query y lo mapea en el objeto ProjectWihtoutOrdersMapper
        return query(query, new ProjectWihtoutInvoicesMapper());
    }

    private List<String> projectsWithoutInvoiceFilter(String filter, Integer report, Boolean orderCanceled, List<String> pKeys) {
        log.debug("Checking filters, filter: {}, orderCanceled: {}", filter, orderCanceled);
        List<String> conditions = new ArrayList<>();

        addGeneralFilter(filter, conditions);

        conditions.add( orderCanceled ? filterOrderCanceled : filterOrderNotCanceled );

        if( report == 1 ) { // Construccion pendiente de pago
            conditions.add(filterApplicationInvoceDeveloped);
        } else if( report == 2 ){ // Instalacion pendiente de pago
            conditions.add(filterProjectInvoiceInstalled);
        } else if( report == 3 ) { // Monitoreo pendiente de pago
            conditions.add(filterProjectInvoiceMonitoring);
        }

        addPKeysIn(pKeys, conditions);

        log.debug("Conditions generated? {}", conditions.size());
        return conditions;
    }
}
