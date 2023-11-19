package com.mexico.sas.admin.api.service;

import com.mexico.sas.admin.api.dto.sso.SSORequestDto;
import com.mexico.sas.admin.api.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Oziel Naranjo
 */
@SpringBootTest
@TestPropertySource(locations = "/env-test.properties")
public class UserServiceTest {

    @Autowired
    private UserService service;

    @Autowired
    private SSOService ssoService;

    private String token;

    @BeforeEach
    public void init() throws CustomException {
        SSORequestDto ssoRequestDto = new SSORequestDto("admin@sas-mexico.com", "12345678");
        token = ssoService.login(ssoRequestDto).getAccessToken();
    }
}
