package com.mexico.sas.nativequeries.api.model.mapper;

import com.mexico.sas.nativequeries.api.model.ProjectPlanHeader;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProjectPlanHeaderMapper implements RowMapper<ProjectPlanHeader> {
    @Override
    public ProjectPlanHeader mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new ProjectPlanHeader(
                rs.getString("p_key"),
                rs.getString("description"),
                rs.getString("pm_name"),
                rs.getString("pm_mail")
        );
    }
}
