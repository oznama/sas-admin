package com.mexico.sas.nativequeries.api.repository;

public interface SQLConstants {
    String PG_FUNCTION_ACCENT = "f_unaccent";
    String WHERE = "where";
    String AND = "and";
    String UNION = "union";
    String COMMA = ",";

    String SUBQUERY_PARAMETER = ":subquery";
    String CONCAT_LIMIT_PATTERN = "%s %s";
    String LIMIT_SIZE_PARAMETER = ":size";
    String LIMIT_PAGE_PARAMETER = ":page";
    String WHERE_CLAUSE_PARAMETER = ":where_clause";
    String FILTER_PARAMETER = ":filter";
    String PROJECT_PKEY_PARAMETER = ":pKey";
    String PROJECT_PKEYS_PARAMETER = ":pKeys";
    String PROJECT_APPNAMES_PARAMETER = ":pApps";
    String PERCENTAGE_PARAMETER = ":percent";
    String EQUAL_REGEX = "'%s'";
    String LIKE_REGEX = "'%%%s%%'";
    String IN_REGEX = "'%s'";

    String WHERE_CONDITION_PATTERN = " %s (%s) ";
}
