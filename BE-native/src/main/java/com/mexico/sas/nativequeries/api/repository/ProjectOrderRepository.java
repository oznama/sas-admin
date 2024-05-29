package com.mexico.sas.nativequeries.api.repository;

import com.mexico.sas.nativequeries.api.model.ProjectWithoutOrders;
import com.mexico.sas.nativequeries.api.model.mapper.ProjectWihtoutOrdersMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class ProjectOrderRepository extends BaseRepository {

    @Value("${query.project.without.orders}")
    private String queryProjectWithoutOrders;

    @Value("${query.project.application.startdate.expired}")
    private String filterStartDateExpired;

    @Value("${query.project.application.status}")
    private String filterApplicationStatus;

    public Page<ProjectWithoutOrders> findProjectsWithoutOrders(String filter, Long paStatus, Pageable pageable) {
        log.debug("findProjectsWithoutOrders Pagged...");
        List<ProjectWithoutOrders> list = new ArrayList<>();
        // Procesar si hay filtros para crear las condiciones del query
        List<String> conditions = projectsWithoutOdersFilter(true, filter, paStatus, null);
        String query = queryProjectWithoutOrders
                .replace(SQLConstants.WHERE_CLAUSE_PARAMETER, !conditions.isEmpty() ? whereClauseBuilder(conditions) : "");
        Long total = queryForObject(queryCount(query), Long.class);
        log.debug("{} row found!", total);
        if( total > 0 ) {
            query = queryPagged(query, pageable.getPageSize(), pageable.getPageNumber() * pageable.getPageSize());
            log.debug("Query: {}", query);
            list = query(query, new ProjectWihtoutOrdersMapper());
        }
        return new PageImpl<>(list, pageable, total);
    }

    public List<ProjectWithoutOrders> findProjectsWithoutOrders(List<String> pKeys) {
        log.debug("findProjectsWithoutOrders with pKeys...");
        return execute(projectsWithoutOdersFilter(false, null, null, pKeys));
    }

    private List<ProjectWithoutOrders> execute(List<String> conditions) {
        // Si hay filtros, se agregan al query si no, no queda vacio
        String query = queryProjectWithoutOrders
                .replace(SQLConstants.WHERE_CLAUSE_PARAMETER, !conditions.isEmpty() ? whereClauseBuilder(conditions) : "");
        log.debug("Query: {}", query);
        // Executa el query y lo mapea en el objeto ProjectWihtoutOrdersMapper
        return query(query, new ProjectWihtoutOrdersMapper());
    }

    private List<String> projectsWithoutOdersFilter(boolean reqConditions, String filter, Long paStatus, List<String> pKeys) {
        log.debug("Checking filters, filter: {}, paStatus: {}", filter, paStatus);
        List<String> conditions = new ArrayList<>();

        // Condiciones obligatorias
        if( reqConditions ) {
            // Esta no lleva parametro asi que no se reemplaza nada
            conditions.add(filterStartDateExpired);
        }

        addGeneralFilter(filter, conditions);

        // Si hay valor en filtro paStatus
        if( paStatus != null ) {
            String paStatusCondition = filterApplicationStatus
                    .replaceAll(SQLConstants.PROJECT_APP_STATUS_PARAMETER, String.valueOf(paStatus));
            conditions.add(paStatusCondition);
        }

        addPKeysIn(pKeys, conditions);

        log.debug("Conditions generated? {}", conditions.size());
        return conditions;
    }
}
