package com.mexico.sas.nativequeries.api.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
public class BaseRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${query.count}")
    private String queryCount;

    @Value("${query.condition.limit}")
    private String conditionLimit;

    @Value("${query.project.general.filter}")
    private String filterGeneral;

    @Value("${query.project.pkeys.list.in}")
    private String filterPkeysListIn;

    protected String queryCount(String query) {
        log.debug("Creating query counter ...");
        return queryCount.replace(SQLConstants.SUBQUERY_PARAMETER, query);
    }

    protected String queryPagged(String query, int limit, int offset) {
        log.debug("Building query, limit: {}, offset: {}", limit, offset);
        return String.format(SQLConstants.CONCAT_LIMIT_PATTERN, query,
                conditionLimit
                        .replace(SQLConstants.LIMIT_SIZE_PARAMETER, String.valueOf(limit))
                        .replace(SQLConstants.LIMIT_PAGE_PARAMETER, String.valueOf(offset))
        );
    }

    protected StringBuilder whereClauseBuilder(List<String> conditions) {
        log.debug("Building where clause ...");
        StringBuilder conditionBuilder = new StringBuilder();
        for( int i=0; i< conditions.size(); i++ ) {
            conditionBuilder.append( String.format( SQLConstants.WHERE_CONDITION_PATTERN,
                    i == 0 ? SQLConstants.WHERE : SQLConstants.AND, conditions.get(i) ) );
        }
        return conditionBuilder;
    }

    protected String inClauseBuilder(List<String> ids) {
        log.debug("Building in clause with {} ids...", ids.size());
        StringBuilder inBuilder = new StringBuilder();
        ids.forEach( id -> inBuilder.append(String.format(SQLConstants.IN_REGEX, id)).append(","));
        return inBuilder.length() > 0 ? inBuilder.toString().substring(0, inBuilder.length()-1) : "";
    }

    protected void addGeneralFilter(String filter, List<String> conditions) {
        // Si hay valor en filtro
        if( !StringUtils.isEmpty(filter) ) {
            String filterCondition = filterGeneral
                    .replaceAll(SQLConstants.FILTER_PARAMETER, String.format(SQLConstants.LIKE_REGEX, filter.toLowerCase()));
            conditions.add(filterCondition);
        }
    }

    protected void addPKeysIn(List<String> pKeys, List<String> conditions) {
        if( pKeys != null ) {
            String paInCondition = filterPkeysListIn
                    .replaceAll(SQLConstants.PROJECT_PKEYS_PARAMETER, inClauseBuilder(pKeys));
            conditions.add(paInCondition);
        }
    }

    protected <T> T queryForObject(String query, Class<T> clazz) {
        log.debug("Query for Object: {}", query);
        return jdbcTemplate.queryForObject(query, clazz);
    }

    protected <T> List<T> query(String query, RowMapper<T> rowMapper) {
        log.debug("Query: {}", query);
        return jdbcTemplate.query(query, rowMapper);
    }

    protected <T> List<T> getForList(String query, Class<T> clazz) {
        log.debug("Query for List: {}", query);
        return jdbcTemplate.queryForList(query, clazz);
    }

}
