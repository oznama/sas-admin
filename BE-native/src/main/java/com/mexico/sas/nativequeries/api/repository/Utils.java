package com.mexico.sas.nativequeries.api.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Slf4j
public class Utils {

    @Value("${query.count}")
    private String queryCount;

    @Value("${query.condition.limit}")
    private String conditionLimit;

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

}
