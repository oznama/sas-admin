package com.mexico.sas.admin.api.service;

import com.mexico.sas.admin.api.exception.LoginException;
import com.mexico.sas.admin.api.security.CustomJWT;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * @author Oziel Naranjo
 */
@SpringBootTest
@TestPropertySource(locations = "/env-test.properties")
public class CustomJWTTest {

    @Autowired
    private CustomJWT customJWT;

    @DisplayName("Spring test generate token")
    @Test
    public void dateInHoursValid() throws LoginException {
        String username = "oziel.naranjo@sas-mexico.com";
        String token = customJWT.getToken(username);
        System.out.printf("Token\n%s\n", token);
        assertDoesNotThrow( () -> customJWT.getClaims(token));
    }
}
