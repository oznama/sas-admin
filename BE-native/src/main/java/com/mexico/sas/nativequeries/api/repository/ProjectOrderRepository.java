package com.mexico.sas.nativequeries.api.repository;

import com.mexico.sas.nativequeries.api.model.ProjectWithApplication;
import com.mexico.sas.nativequeries.api.model.mapper.ProjectWihtApplicationMapper;
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
public class ProjectOrderRepository extends BaseRepository {

    @Value("${query.project.field.key}")
    private String fieldProjectKey;
    @Value("${query.project.without.orders.fields}")
    private String fieldsProjectWithoutOrders;
    @Value("${query.project.without.orders}")
    private String queryProjectWithoutOrders;

    @Value("${query.project.order.general}")
    private String orderGeneral;

    @Value("${query.project.application.startdate.expired}")
    private String filterStartDateExpired;

    public Page<ProjectWithApplication> findProjectsWithoutOrders(String filter, Pageable pageable) {
        log.debug("findProjectsWithoutOrders Pagged...");
        List<ProjectWithApplication> list = new ArrayList<>();
        // Procesar si hay filtros para crear las condiciones del query
        List<String> conditions = projectsWithoutOdersFilter(true, filter, null);
        String query = fieldsProjectWithoutOrders.concat(" ")
                .concat(queryProjectWithoutOrders)
                .replace(SQLConstants.WHERE_CLAUSE_PARAMETER, !conditions.isEmpty() ? whereClauseBuilder(conditions) : "")
                .concat(" ").concat(orderGeneral);
        Long total = queryForObject(queryCount(query), Long.class);
        log.debug("{} row found!", total);
        if( total > 0 ) {
            query = queryPagged(query, pageable.getPageSize(), pageable.getPageNumber() * pageable.getPageSize());
            list = query(query, new ProjectWihtApplicationMapper());
        }
        return new PageImpl<>(list, pageable, total);
    }

    public List<ProjectWithApplication> findProjectsWithoutOrders(List<String> pKeys) {
        log.debug("findProjectsWithoutOrders with pKeys...");
        return execute(projectsWithoutOdersFilter(false, null, pKeys));
    }

    public List<String> findProjectsWithoutOrders(String filter) {
        log.debug("findProjectsWithoutOrders keys...");
        List<String> conditions = projectsWithoutOdersFilter(true, filter, null);
        String query = fieldProjectKey.concat(" ")
                .concat(queryProjectWithoutOrders)
                .replace(SQLConstants.WHERE_CLAUSE_PARAMETER, !conditions.isEmpty() ? whereClauseBuilder(conditions) : "")
                .concat(" ").concat(orderGeneral);
        return getForList(query, String.class);
    }

    private List<ProjectWithApplication> execute(List<String> conditions) {
        // Si hay filtros, se agregan al query si no, no queda vacio
        String query = fieldsProjectWithoutOrders.concat(" ")
                .concat(queryProjectWithoutOrders)
                .replace(SQLConstants.WHERE_CLAUSE_PARAMETER, !conditions.isEmpty() ? whereClauseBuilder(conditions) : "")
                .concat(" ").concat(orderGeneral);
        // Executa el query y lo mapea en el objeto ProjectWihtoutOrdersMapper
        return query(query, new ProjectWihtApplicationMapper());
    }

    private List<String> projectsWithoutOdersFilter(boolean reqConditions, String filter, List<String> pKeys) {
        log.debug("Checking filters, filter: {}", filter);
        List<String> conditions = new ArrayList<>();

        // Condiciones obligatorias
        if( reqConditions ) {
            // Esta no lleva parametro asi que no se reemplaza nada
            conditions.add(filterStartDateExpired);
        }
        addGeneralFilter(filter, conditions);
        addPKeysIn(pKeys, conditions);

        log.debug("Conditions generated? {}", conditions.size());
        return conditions;
    }
}
