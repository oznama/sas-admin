package com.mexico.sas.admin.api.constants;

/**
 * @author Oziel Naranjo
 */
public interface GeneralKeys {

    String CONSTANT_BEARER = "Bearer ";

    String PERMISSION_SPECIAL = "SPECIAL";

    int ONE_SECOND_IN_MILLIS = 1000;
    int ONE_MINUTE_IN_MILLIS = (60 * ONE_SECOND_IN_MILLIS);

    int ONE_MEGABYTES_TO_BYTES = 1048576;

    String PG_FUNCTION_ACCENT = "f_unaccent";
    String PATTERN_LIKE = "%%%s%%";


    double PERC_100 = 100;

    int REPORT_PERIOD_MONTH_VALID  = 3;

    String TMP_PATH = "/tmp";
    String FORMAT_DATE_FOR_NAME = "yyyyMMdd_HHmmss";

    String REQUEST_PROPERTY_USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11";

    Long ROOT_USER_ID = 1l; // User root id, and too Admin Role Id

    String DIAGONAL = "/";

    String PSW_TEMPORAL = "Ea6SCcSRF9rJUxhtMk3bXg=="; // 12345678 with current enc
}
