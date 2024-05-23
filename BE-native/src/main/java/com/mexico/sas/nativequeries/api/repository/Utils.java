package com.mexico.sas.nativequeries.api.repository;

import lombok.extern.slf4j.Slf4j;

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
}
