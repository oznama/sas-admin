package com.mexico.sas.nativequeries.api.repository;

import com.mexico.sas.nativequeries.api.model.ProjectWithoutOrders;
import com.mexico.sas.nativequeries.api.model.mapper.ProjectWihtoutOrdersMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class ProjOrdRepository extends Utils {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${query.project.without.orders}")
    private String queryProjectWithoutOrders;
    @Value("${query.project.without.orders.where01}")
    private String cond01ProjectWithoutOrders;

    @Value("${query.project.without.orders.where02}")
    private String condO2ProjectWithoutOrders;

    public List<ProjectWithoutOrders> findProjectsWithoutOrders(String filter, Long paStatus) {
        log.debug("findProjectsWithoutOrders...");

        // Procesar si hay filtros para crear las condiciones del query
        List<String> conditions = projectsWithoutOdersFilter(filter, paStatus);

        // Si hay filtros, se agregan al query si no, no queda vacio
        String query = queryProjectWithoutOrders
                .replace(SQLConstants.WHERE_CLAUSE_PARAMETER, !conditions.isEmpty() ? whereClauseBuilder(conditions) : "");

        log.debug("Query: {}", query);

        // Executa el query y lo mapea en el objeto ProjectWihtoutOrdersMapper
        return jdbcTemplate.query(query, new ProjectWihtoutOrdersMapper());
    }

    private List<String> projectsWithoutOdersFilter(String filter, Long paStatus) {
        log.debug("Checking filters, filter: {}, paStatus: {}", filter, paStatus);
        List<String> conditions = new ArrayList<>();

        // Si hay valor en filtro
        if( !StringUtils.isEmpty(filter) ) {
            String filterCondition = cond01ProjectWithoutOrders
                    .replaceAll(SQLConstants.FILTER_PARAMETER, String.format(SQLConstants.LIKE_REGEX, filter.toLowerCase()));
            conditions.add(filterCondition);
        }

        // Si hay valor en filtro paStatus
        if( paStatus != null ) {
            String paStatusCondition = condO2ProjectWithoutOrders
                    .replaceAll(SQLConstants.PROJECT_APP_STATUS_PARAMETER, String.valueOf(paStatus));
            conditions.add(paStatusCondition);
        }
        log.debug("Conditions generated? {}", conditions.size());
        return conditions;
    }
}
