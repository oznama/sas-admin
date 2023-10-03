package com.mexico.sas.admin.api.security;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Oziel Naranjo
 */
public class TokenBlackList {

    /**
     * TODO Implementation temp
     */

    private static final Map<String, Boolean> MAP = new HashMap<>();

    public static void add(String token) {
        MAP.put(token, Boolean.TRUE); // Enabled
    }

    public static void inactivate(String token) {
        MAP.replace(token, Boolean.FALSE); // Disabled
    }

    public static Boolean isValid(String token) {
        return MAP.containsKey(token) && MAP.get(token);
    }
}
