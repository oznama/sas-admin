package com.mexico.sas.admin.api.repository;

import com.mexico.sas.admin.api.model.Project;
import io.jsonwebtoken.lang.Collections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "/env-test.properties")
@Sql("/script.sql")
public class ProjectRepositoryTest {

    @Autowired
    private ProjectRepository repository;
    private boolean error;

    @DisplayName("Spring Test Project save by repository")
    @Test
    public void save() {
        Project project = new Project();
        repository.save(project);
        assertNotNull(project.getId());
    }

    @DisplayName("Spring Test Project update by repository")
    @Test
    public void update() throws Exception {
        Project project = new Project();
        repository.save(project);
        assertNull(project.getName()); // El nombre este nulo
        project.setName("T-XXXX-XX-XX");
        assertDoesNotThrow( () -> repository.save(project) ); // El update no truene
        Project projectWithName = repository.findById(project.getId()).orElseThrow( Exception::new );
        assertFalse(projectWithName.getName().isEmpty()); // El objeto consultado tenga el valor del name
        assertTrue(projectWithName.getName().equals("T-XXXX-XX-XX")); // Validar que el texto sea el que guardamos
    }


}
