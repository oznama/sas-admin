package com.mexico.sas.nativequeries.api.model.mapper;

import com.mexico.sas.nativequeries.api.model.ProjectPlanDetail;
import com.mexico.sas.nativequeries.api.repository.BaseRepository;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProjectPlanDetailMapper implements RowMapper<ProjectPlanDetail> {
    @Override
    public ProjectPlanDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new ProjectPlanDetail(
                rs.getString("app_name"),
                rs.getString("leader_name"),
                rs.getString("leader_mail"),
                rs.getString("developer_name"),
                rs.getString("developer_mail"),
                BaseRepository.parseDateText(rs.getDate("start_date")),
                BaseRepository.parseDateText(rs.getDate("design_date")),
                BaseRepository.parseDateText(rs.getDate("development_date")),
                BaseRepository.parseDateText(rs.getDate("end_date"))
        );
    }
}
