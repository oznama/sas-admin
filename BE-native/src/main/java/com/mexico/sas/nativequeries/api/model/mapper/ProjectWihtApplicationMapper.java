package com.mexico.sas.nativequeries.api.model.mapper;

import com.mexico.sas.nativequeries.api.model.ProjectWithApplication;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProjectWihtApplicationMapper implements RowMapper<ProjectWithApplication> {
    @Override
    public ProjectWithApplication mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new ProjectWithApplication(
                rs.getString("project_key"),
                rs.getString("project_name"),
                rs.getString("pm_mail"),
                rs.getString("pm_name"),
                rs.getString("boss_mail"),
                rs.getString("boss_name"),
                rs.getLong("num_orders"),
                rs.getString("project_amount"),
                rs.getString("tax"),
                rs.getString("total"),
                rs.getString("installation"),
                rs.getString("monitoring")
        );
    }
}
