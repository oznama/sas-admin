package com.mexico.sas.nativequeries.api.model.mapper;

import com.mexico.sas.nativequeries.api.model.ProjectAppCat;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProjectAppCatMapper implements RowMapper<ProjectAppCat> {
    @Override
    public ProjectAppCat mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new ProjectAppCat(
                rs.getString("app_name"),
                rs.getString("leader_name"),
                rs.getString("developer_name")
        );
    }
}
