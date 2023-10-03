package com.mexico.sas.admin.api.constants;

/**
 * @author Oziel Naranjo
 * Reference:
 * Validator online: https://regex101.com/
 */
public interface Regex {

    String ONLY_LETTERS = "^$|^[a-zA-ZÀ-ú]*$";
    String ONLY_LETTERS_WHIT_SPACE = "^$|^([A-zÀ-ú]+\\s)*[A-zÀ-ú]+$";
    String ONLY_NUMBERS = "^[0-9]*$";
    String PHONE_NUMBER = "^\\+*\\d{1,14}$";

    // Password validations
    String PASSWORD_VALIDATION_NUMBER = "(.*[0-9].*)";
    String PASSWORD_VALIDATION_UPPERCASECHARS = "(.*[A-Z].*)";
    String PASSWORD_VALIDATION_LOWERCASECHARS = "(.*[a-z].*)";
    String PASSWORD_VALIDATION_SPECIALCHARS = "(.*[@,#,$,%].*$)";
    String PASSWORD_VALIDATION_STRONG = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>_%.¡¿]).{8,16}$";

    String LETTERS_WITH_SPACE = "^([A-zÀ-ú]+\\s)*[A-zÀ-ú]+$";
    String ALPHANUMERIC = "^[a-zA-ZÀ-ú0-9.]*$";
    String ALPHANUMERIC_WITH_SPACE = "^([A-zÀ-ú0-9]+\\s)*[A-zÀ-ú0-9]+$";
    String URL = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]$";
    String DATE_DDMMYYYY = "^(3[01]|[12][0-9]|0[1-9])/(1[0-2]|0[1-9])/[0-9]{4}$";

    String RFC = "^([A-Z,Ñ,&]{3,4}([0-9]{2})(0[1-9]|1[0-2])(0[1-9]|1[0-9]|2[0-9]|3[0-1])[A-Z|\\d]{3})$";

}
