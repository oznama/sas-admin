package com.mexico.sas.nativequeries.api.model.mapper;

import com.mexico.sas.nativequeries.api.model.ProjectWithoutOrders;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProjectWihtoutOrdersMapper implements RowMapper<ProjectWithoutOrders> {
    @Override
    public ProjectWithoutOrders mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new ProjectWithoutOrders(
                rs.getString("clave"),
                rs.getString("proyecto"),
                rs.getString("pm_correo"),
                rs.getString("pm_nombre"),
                rs.getLong("ordenes"),
                rs.getBigDecimal("costo_proyecto")
        );
    }
}
