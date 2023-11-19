package com.mexico.sas.admin.api.service;

import com.mexico.sas.admin.api.dto.project.ProjectDto;
import com.mexico.sas.admin.api.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Oziel Naranjo
 */
@SpringBootTest
@TestPropertySource(locations = "/env-test.properties")
public class ProjectServiceTest {

    @Autowired
    private ProjectService service;

    @DisplayName("Spring Test Project service save")
    @Test
    public void findById() throws CustomException {
        ProjectDto projectDto = new ProjectDto();
        projectDto.setKey("CLAVE_01");
        projectDto.setDescription("Migración de BINES a 8 Posiciones para BANORTE");
        projectDto.setCompanyId(1l);
        projectDto.setProjectManagerId(1l);
        service.save(projectDto);
        assertNotNull(projectDto.getId());
    }
}
