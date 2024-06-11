package com.mexico.sas.nativequeries.api.repository;

import com.mexico.sas.nativequeries.api.model.ProjectWithoutInvoices;
import com.mexico.sas.nativequeries.api.model.mapper.ProjectWihtoutInvoicesMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@Slf4j
public class ProjectInvoiceRepository extends BaseRepository {

    @Value("${query.project.field.key}")
    private String fieldProjectKey;
    @Value("${query.project.without.invoice.fields}")
    private String fieldsProjectWithoutInvoice;
    @Value("${query.project.without.invoice}")
    private String queryProjectWithoutInvoice;

    @Value("${query.project.order.general}")
    private String orderGeneral;

    @Value("${query.project.order.union}")
    private String orderUnion;

    @Value("${query.project.order.status.canceled}")
    private String filterOrderCanceled;

    @Value("${query.project.order.status.not.canceled}")
    private String filterOrderNotCanceled;

    @Value("${query.project.application.invoice.developed}")
    private String filterApplicationInvoceDeveloped;

    @Value("${query.project.invoice.installed}")
    private String filterProjectInvoiceInstalled;

    @Value("${query.project.invoice.monitoring}")
    private String filterProjectInvoiceMonitoring;

    public Page<ProjectWithoutInvoices> findProjectsWithoutInvoices(String filter, Integer report, Boolean orderCanceled, Integer percentage, Pageable pageable) {
        log.debug("findProjectsWithoutInvoice Pagged of report: {} with percentage: {} and orderCanceled?: {}...",
                report, percentage, orderCanceled);
        List<ProjectWithoutInvoices> list = new ArrayList<>();
        List<String> conditions = projectsWithoutInvoiceFilter(filter, report, orderCanceled, null);
        String query = fieldsProjectWithoutInvoice.concat(" ")
                .concat(queryProjectWithoutInvoice)
                .replace(SQLConstants.WHERE_CLAUSE_PARAMETER, !conditions.isEmpty() ? whereClauseBuilder(conditions) : "")
                .replaceAll(SQLConstants.PERCENTAGE_PARAMETER, String.valueOf(percentage))
                .concat(" ").concat(orderGeneral);
        Long total = queryForObject(queryCount(query), Long.class);
        log.debug("{} row found!", total);
        if( total > 0 ) {
            query = queryPagged(query, pageable.getPageSize(), pageable.getPageNumber() * pageable.getPageSize());
            list = query(query, new ProjectWihtoutInvoicesMapper());
        }
        return new PageImpl<>(list, pageable, total);
    }

    public List<ProjectWithoutInvoices> findProjectsWithoutInvoices(Integer report, Boolean orderCanceled, Integer percentage, List<String> pKeys) {
        log.debug("findProjectsWithoutInvoce List with pKeys of report: {} and orderCanceled?: {}...", report, orderCanceled);
        if( report >= 1 && report <= 3 ) {
            return execute(percentage, projectsWithoutInvoiceFilter( null, report, orderCanceled, pKeys));
        } else if ( report == 4 ) {
            return buildAndExceuteUnion(orderCanceled, pKeys);
        } else {
            log.warn("Report {} not supported", report);
            return Collections.emptyList();
        }
    }

    public List<String> findProjectsWithoutInvoices(String filter, Integer report, Boolean orderCanceled, Integer percentage) {
        log.debug("findProjectsWithoutInvoce keys of report: {} and orderCanceled?: {}...", report, orderCanceled);
        if( report >= 1 && report <= 3 ) {
            return execute(filter, report, orderCanceled, percentage);
        } else if ( report == 4 ) {
            return buildAndExceuteUnion(filter, orderCanceled);
        } else {
            log.warn("Report {} not supported", report);
            return Collections.emptyList();
        }
    }

    private List<ProjectWithoutInvoices> execute(Integer percentage, List<String> conditions) {
        String query = fieldsProjectWithoutInvoice.concat(" ")
                .concat(queryProjectWithoutInvoice)
                .replace(SQLConstants.WHERE_CLAUSE_PARAMETER, !conditions.isEmpty() ? whereClauseBuilder(conditions) : "")
                .replaceAll(SQLConstants.PERCENTAGE_PARAMETER, String.valueOf(percentage))
                .concat(" ").concat(orderGeneral);
        return query(query, new ProjectWihtoutInvoicesMapper());
    }

    private List<String> execute(String filter, Integer report, Boolean orderCanceled, Integer percentage) {
        List<String> conditions = projectsWithoutInvoiceFilter(filter, report, orderCanceled, null);
        String query = fieldProjectKey.concat(" ")
                .concat(queryProjectWithoutInvoice)
                .replace(SQLConstants.WHERE_CLAUSE_PARAMETER, !conditions.isEmpty() ? whereClauseBuilder(conditions) : "")
                .replaceAll(SQLConstants.PERCENTAGE_PARAMETER, String.valueOf(percentage))
                .concat(" ").concat(orderGeneral);
        return getForList(query, String.class);
    }

    private List<ProjectWithoutInvoices> buildAndExceuteUnion(Boolean orderCanceled, List<String> pKeys) {
        log.debug("Building union query ...");
        Map<Integer, Integer> reports = new HashMap<>();
        reports.put(1, 30);
        reports.put(2, 60);
        reports.put(3, 100);
        StringBuilder queryBuilder = new StringBuilder();
        reports.forEach((report, percentage) -> {
            log.debug("Loop in report {} with percentage {}", report, percentage);
            List<String> conditions = projectsWithoutInvoiceFilter(null, report, orderCanceled, pKeys);
            queryBuilder.append(fieldsProjectWithoutInvoice.concat("").concat(queryProjectWithoutInvoice)
                    .replace(SQLConstants.WHERE_CLAUSE_PARAMETER, !conditions.isEmpty() ? whereClauseBuilder(conditions) : "")
                    .replaceAll(SQLConstants.PERCENTAGE_PARAMETER, String.valueOf(percentage)));
            if( report < 3 ) {
                log.debug("Appending union word");
                queryBuilder.append(" ").append(SQLConstants.UNION).append(" ");
            }
        });
        queryBuilder.append(" ").append(orderUnion);
        return query(queryBuilder.toString(), new ProjectWihtoutInvoicesMapper());
    }

    private List<String> buildAndExceuteUnion(String filter, Boolean orderCanceled) {
        log.debug("Building union query ...");
        Map<Integer, Integer> reports = new HashMap<>();
        reports.put(1, 30);
        reports.put(2, 60);
        reports.put(3, 100);
        StringBuilder queryBuilder = new StringBuilder();
        reports.forEach((report, percentage) -> {
            log.debug("Loop in report {} with percentage {}", report, percentage);
            List<String> conditions = projectsWithoutInvoiceFilter(filter, report, orderCanceled, null);
            queryBuilder.append(fieldProjectKey.concat(" ").concat(queryProjectWithoutInvoice)
                    .replace(SQLConstants.WHERE_CLAUSE_PARAMETER, !conditions.isEmpty() ? whereClauseBuilder(conditions) : "")
                    .replaceAll(SQLConstants.PERCENTAGE_PARAMETER, String.valueOf(percentage)));
            if( report < 3 ) {
                log.debug("Appending union word");
                queryBuilder.append(" ").append(SQLConstants.UNION).append(" ");
            }
        });
        queryBuilder.append(" ").append(orderUnion);
        return getForList(queryBuilder.toString(), String.class);
    }

    private List<String> projectsWithoutInvoiceFilter(String filter, Integer report, Boolean orderCanceled, List<String> pKeys) {
        log.debug("Checking filters, filter: {}, report: {}, orderCanceled: {}", filter, report, orderCanceled);
        List<String> conditions = new ArrayList<>();

        addGeneralFilter(filter, conditions);

        if( orderCanceled ) {
            conditions.add( filterOrderCanceled );
        } else {
            conditions.add( filterOrderNotCanceled );
            if( report == 1 ) { // Construccion pendiente de pago
                conditions.add(filterApplicationInvoceDeveloped);
            } else if( report == 2 ){ // Instalacion pendiente de pago
                conditions.add(filterProjectInvoiceInstalled);
            } else if( report == 3 ) { // Monitoreo pendiente de pago
                conditions.add(filterProjectInvoiceMonitoring);
            }
        }

        addPKeysIn(pKeys, conditions);

        log.debug("Conditions generated? {}", conditions.size());
        return conditions;
    }
}
