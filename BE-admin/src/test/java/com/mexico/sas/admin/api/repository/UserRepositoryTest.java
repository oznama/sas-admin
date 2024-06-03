package com.mexico.sas.admin.api.repository;

import com.mexico.sas.admin.api.model.Company;
import com.mexico.sas.admin.api.model.Employee;
import com.mexico.sas.admin.api.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Oziel Naranjo
 */
@SpringBootTest
@TestPropertySource(locations = "/env-test.properties")
@Sql("/script.sql")
public class UserRepositoryTest {

    @Autowired
    private UserRepository repository;

    @DisplayName("Spring Test User Find All Users")
    @Test
    public void findAll() {
        List<User> users = repository.findAll();
        assertFalse(users.isEmpty());
        users.forEach( user -> {
            System.out.printf("User: %d, Employee id: %d", user.getId(), user.getEmployeeId());
        });
    }

    @DisplayName("Spring Test User Find by Id")
    @Test
    public void findById() throws Exception {
        User user = repository.findById(2L).orElseThrow( () -> new Exception("User not find"));
        assertNotNull(user);
        System.out.printf("Usuario: %d\n", user.getId());
    }
}
