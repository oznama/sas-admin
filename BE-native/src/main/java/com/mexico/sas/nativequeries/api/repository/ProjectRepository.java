package com.mexico.sas.nativequeries.api.repository;

import com.mexico.sas.nativequeries.api.model.ProjectAppCat;
import com.mexico.sas.nativequeries.api.model.ProjectWithApplication;
import com.mexico.sas.nativequeries.api.model.mapper.ProjectAppCatMapper;
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
public class ProjectRepository extends BaseRepository {

    @Value("${query.project.plan.apps.names}")
    private String queryProjectPlanAppsNames;

    @Value("${query.project.field.key}")
    private String fieldProjectKey;

    @Value("${query.project.with.applications.fields}")
    private String fieldsProjectWithApplications;

    @Value("${query.project.with.applications}")
    private String queryProjectWithApplications;

    @Value("${query.project.order.general}")
    private String orderGeneral;

    @Value("${query.project.installed.isnull}")
    private String filterInstalledIsNull;

    @Value("${query.project.monitoring.isnull}")
    private String filterMonitoringIsNull;

    public List<ProjectAppCat> getProjectPlanAppsNames(String pKey) {
        return query(queryProjectPlanAppsNames.replace(SQLConstants.PROJECT_PKEY_PARAMETER, String.format(SQLConstants.EQUAL_REGEX, pKey)), new ProjectAppCatMapper());
    }

    public Page<ProjectWithApplication> findProjectsWithApplication(String filter, Boolean installed, Boolean monitoring, Pageable pageable) {
        log.debug("findProjectsWithApplication Pagged...");
        List<ProjectWithApplication> list = new ArrayList<>();
        // Procesar si hay filtros para crear las condiciones del query
        List<String> conditions = projectsWithApplicationFilter(filter, installed, monitoring, null);
        String query = fieldsProjectWithApplications.concat(" ")
                .concat(queryProjectWithApplications)
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

    public List<ProjectWithApplication> findProjectsWithApplication(Boolean installed, Boolean monitoring, List<String> pKeys) {
        log.debug("findProjectsWithApplication with pKeys...");
        return execute(projectsWithApplicationFilter(null, installed, monitoring, pKeys));
    }

    public List<String> findProjectsWithApplication(String filter, Boolean installed, Boolean monitoring) {
        log.debug("findProjectsWithApplication keys...");
        List<String> conditions = projectsWithApplicationFilter(filter, installed, monitoring, null);
        String query = fieldProjectKey.concat(" ")
                .concat(queryProjectWithApplications)
                .replace(SQLConstants.WHERE_CLAUSE_PARAMETER, !conditions.isEmpty() ? whereClauseBuilder(conditions) : "")
                .concat(" ").concat(orderGeneral);
        return getForList(query, String.class);
    }

    private List<ProjectWithApplication> execute(List<String> conditions) {
        // Si hay filtros, se agregan al query si no, no queda vacio
        String query = fieldsProjectWithApplications.concat(" ")
                .concat(queryProjectWithApplications)
                .replace(SQLConstants.WHERE_CLAUSE_PARAMETER, !conditions.isEmpty() ? whereClauseBuilder(conditions) : "")
                .concat(" ").concat(orderGeneral);
        // Executa el query y lo mapea en el objeto ProjectWihtoutOrdersMapper
        return query(query, new ProjectWihtApplicationMapper());
    }

    private List<String> projectsWithApplicationFilter(String filter, Boolean installed, Boolean monitoring, List<String> pKeys) {
        log.debug("Checking filters, filter: {}, installed: {}, monitoring: {}", filter, installed, monitoring);
        List<String> conditions = new ArrayList<>();

        addGeneralFilter(filter, conditions);

        if( installed ) {
            conditions.add(filterInstalledIsNull);
        }
        if( monitoring ) {
            conditions.add(filterMonitoringIsNull);
        }

        addPKeysIn(pKeys, conditions);

        log.debug("Conditions generated? {}", conditions.size());
        return conditions;
    }
}
