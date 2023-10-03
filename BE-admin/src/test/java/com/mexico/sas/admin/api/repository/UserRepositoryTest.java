package com.mexico.sas.admin.api.repository;

import com.mexico.sas.admin.api.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Oziel Naranjo
 */
@SpringBootTest
@TestPropertySource(locations = "/env-test.properties")
@Sql("/script.sql")
public class UserRepositoryTest {

    @Autowired
    private UserRepository repository;

    @DisplayName("Spring Test User Find by Id")
    @Test
    public void findById() throws Exception {
        User user = repository.findById(1L).orElseThrow( () -> new Exception("User not find"));
        assertNotNull(user);
    }
}
