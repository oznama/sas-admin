package com.mexico.sas.nativequeries.api.repository;

public interface SQLConstants {
    String PG_FUNCTION_ACCENT = "f_unaccent";
    String WHERE = "where";
    String AND = "and";
    String WHERE_CLAUSE_PARAMETER = ":where_clause";
    String FILTER_PARAMETER = ":filter";
    String PROJECT_APP_STATUS_PARAMETER = ":paStatus";
    String LIKE_REGEX = "'%%%s%%'";
    String WHERE_CONDITION_PATTER = " %s (%s) ";
}
