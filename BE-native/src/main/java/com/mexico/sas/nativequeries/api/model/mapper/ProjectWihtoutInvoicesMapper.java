package com.mexico.sas.nativequeries.api.model.mapper;

import com.mexico.sas.nativequeries.api.model.ProjectWithoutInvoices;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProjectWihtoutInvoicesMapper implements RowMapper<ProjectWithoutInvoices> {
    @Override
    public ProjectWithoutInvoices mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new ProjectWithoutInvoices(
                rs.getString("project_key"),
                rs.getString("project_name"),
                rs.getString("pm_mail"),
                rs.getString("pm_name"),
                rs.getString("boss_mail"),
                rs.getString("boss_name"),
                rs.getLong("num_invoices"),
                rs.getString("project_amount"),
                rs.getString("tax"),
                rs.getString("total")
        );
    }
}
