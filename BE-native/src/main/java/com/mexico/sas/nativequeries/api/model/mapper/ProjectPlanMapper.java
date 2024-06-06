package com.mexico.sas.nativequeries.api.model.mapper;

import com.mexico.sas.nativequeries.api.model.ProjectPlan;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProjectPlanMapper implements RowMapper<ProjectPlan> {
    @Override
    public ProjectPlan mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new ProjectPlan(
                rs.getString("app_name"),
                rs.getString("leader_name"),
                rs.getString("leader_mail"),
                rs.getString("developer_name"),
                rs.getString("developer_mail"),
                rs.getString("pm_name"),
                rs.getString("pm_mail")
        );
    }
}
