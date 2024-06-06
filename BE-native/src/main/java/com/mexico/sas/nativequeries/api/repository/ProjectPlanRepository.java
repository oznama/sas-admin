package com.mexico.sas.nativequeries.api.repository;

import com.mexico.sas.nativequeries.api.model.ProjectPlan;
import com.mexico.sas.nativequeries.api.model.ProjectPlanDetail;
import com.mexico.sas.nativequeries.api.model.ProjectPlanHeader;
import com.mexico.sas.nativequeries.api.model.mapper.ProjectPlanDetailMapper;
import com.mexico.sas.nativequeries.api.model.mapper.ProjectPlanHeaderMapper;
import com.mexico.sas.nativequeries.api.model.mapper.ProjectPlanMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
public class ProjectPlanRepository extends BaseRepository {


    @Value("${query.project.plan.apps}")
    private String queryProjectPlanApps;

    @Value("${query.project.plan.header}")
    private String queryProjectPlanHeader;

    @Value("${query.project.plan.detail}")
    private String queryProjectPlanDetail;

    @Value("${query.project.plan.update.date}")
    private String queryProjectPlanUpdateDate;

    @Value("${query.project.plan.update.date.log}")
    private String queryProjectPlanInsertLog;

    public List<ProjectPlan> getProjectPlanApps(String pKey) {
        return query(queryProjectPlanApps.replace(SQLConstants.PROJECT_PKEY_PARAMETER, String.format(SQLConstants.EQUAL_REGEX, pKey)), new ProjectPlanMapper());
    }

    public ProjectPlanHeader getProjectPlanHeader(String pKey) {
        return queryForObject(queryProjectPlanHeader.replace(SQLConstants.PROJECT_PKEY_PARAMETER, String.format(SQLConstants.EQUAL_REGEX, pKey)), new ProjectPlanHeaderMapper());
    }

    public List<ProjectPlanDetail> getProjectPlanDetail(String pKey, List<String> pNames) {
        return query( queryProjectPlanDetail
                .replace(SQLConstants.PROJECT_PKEY_PARAMETER, String.format(SQLConstants.EQUAL_REGEX, pKey))
                .replace(SQLConstants.PROJECT_APPNAMES_PARAMETER, inClauseBuilder(pNames))
                , new ProjectPlanDetailMapper());
    }

    public void updateDate(String pKey) {
        log.debug("Update project plan date");
        execute(queryProjectPlanUpdateDate.replace(SQLConstants.PROJECT_PKEY_PARAMETER, String.format(SQLConstants.EQUAL_REGEX, pKey)));
        execute(queryProjectPlanInsertLog.replace(SQLConstants.PROJECT_PKEY_PARAMETER, String.format(SQLConstants.EQUAL_REGEX, pKey)));
    }

}
