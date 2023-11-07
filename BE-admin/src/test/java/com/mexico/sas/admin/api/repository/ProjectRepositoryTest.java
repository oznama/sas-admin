package com.mexico.sas.admin.api.repository;

import com.mexico.sas.admin.api.model.Client;
import com.mexico.sas.admin.api.model.Employee;
import com.mexico.sas.admin.api.model.Project;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Oziel Naranjo
 */
@SpringBootTest
@TestPropertySource(locations = "/env-test.properties")
public class ProjectRepositoryTest {

    @Autowired
    private ProjectRepository repository;

    @DisplayName("Spring Test Project repository save")
    @Test
    public void findById() throws Exception {
        Project project = new Project();
        project.setKey("CLAVE_01");
        project.setDescription("Descripcion prueba");
        project.setClient(new Client(1l));
        project.setProjectManager(new Employee(1l));
        project.setUserId(1l);
        project.setCreationDate(new Date());
        project.setStatus(2000200002l);
        repository.save(project);
        assertNotNull(project.getId());
    }
}
