package com.mexico.sas.nativequeries.api.repository;

import com.mexico.sas.nativequeries.api.model.ProjectWithoutOrders;
import com.mexico.sas.nativequeries.api.model.mapper.ProjectWihtoutOrdersMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

@Slf4j
public class Utils {

    protected StringBuilder whereClauseBuilder(List<String> conditions) {
        log.debug("Building where clause ...");
        StringBuilder conditionBuilder = new StringBuilder();
        for( int i=0; i< conditions.size(); i++ ) {
            conditionBuilder.append( String.format( SQLConstants.WHERE_CONDITION_PATTER,
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
