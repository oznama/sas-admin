package com.mexico.sas.admin.api.dummy;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProjectDummyRepository {
    private static final List<ProjectDummy> TABLE = new ArrayList<>();

    static {
        TABLE.add(new ProjectDummy(
                1L,
                "N-2301-13",
                "SAS Admin",
                "Proyecto para administrar las aplicaciones de SAS",
                "02/10/2023",
                "29/03/2024",
                "Selene Pascali",
                "Oziel Naranjo",
                "Internal",
                "Jaime Carreño"
        ));
        TABLE.add(new ProjectDummy(
                2L,
                "P-2003-45",
                "SIO WEB",
                "Punteo Electronico Transaccional",
                "16/03/2020",
                "25/09/2020",
                "Selene Pascali",
                "Oziel Naranjo",
                "Prosa",
                "Juan Perez"
        ));
    }

    public ProjectDummy save(ProjectDummy projectDummy) {
        if( projectDummy.getId() == null ) {
            projectDummy.setId(Long.valueOf((TABLE.size()+1)));
        }
        TABLE.add(projectDummy);
        return findById(projectDummy.getId());
    }

    public List<ProjectDummy> findAll() {
        return TABLE;
    }

    public ProjectDummy findById(Long id) {
        return TABLE.get(TABLE.indexOf(new ProjectDummy(id)));
    }
}
