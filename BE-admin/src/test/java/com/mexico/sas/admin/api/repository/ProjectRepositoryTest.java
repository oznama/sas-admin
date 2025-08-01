package com.mexico.sas.admin.api.repository;

import com.mexico.sas.admin.api.model.Company;
import com.mexico.sas.admin.api.model.Employee;
import com.mexico.sas.admin.api.model.Project;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;

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
        project.setCompany(new Company(2l));
        project.setProjectManager(new Employee(2l));
        project.setAmount(BigDecimal.ZERO);
        project.setTax(BigDecimal.ZERO);
        project.setTotal(BigDecimal.ZERO);
        project.setStatus(2000200001l);
        repository.save(project);
    }
}
