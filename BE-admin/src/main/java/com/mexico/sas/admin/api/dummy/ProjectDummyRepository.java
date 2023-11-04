package com.mexico.sas.admin.api.dummy;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ProjectDummyRepository {
    private static final List<ProjectDummy> TABLE = new ArrayList<>();

    static {
        Map<String, String> dates = getRandomDates();
        Map<String, String> persons = getPersons(1);
        TABLE.add(new ProjectDummy(
                1L,
                "N-2301-13",
                "SAS Admin",
                "Proyecto para administrar las aplicaciones de SAS",
                dates.get("creationDate"),
                dates.get("dueDate"),
                persons.get("createdBy"),
                persons.get("leader"),
                persons.get("client"),
                persons.get("pm"),
                getStatus()
        ));
        dates = getRandomDates();
        persons = getPersons(2);
        TABLE.add(new ProjectDummy(
                2L,
                "P-2003-45",
                "SIO WEB",
                "Punteo Electronico Transaccional",
                dates.get("creationDate"),
                dates.get("dueDate"),
                persons.get("createdBy"),
                persons.get("leader"),
                persons.get("client"),
                persons.get("pm"),
                getStatus()
        ));
        for (int i = 10; i < 700; i++) {
            dates = getRandomDates();
            persons = getPersons(i);
            TABLE.add(new ProjectDummy(
                    Long.valueOf(i),
                    String.format("N-%d-2003-%d", i, i),
                    String.format("Proyecto %d", i),
                    String.format("Proyecto de prueba para validar la descripcion %d", i),
                    dates.get("creationDate"),
                    dates.get("dueDate"),
                    persons.get("createdBy"),
                    persons.get("leader"),
                    persons.get("client"),
                    persons.get("pm"),
                    getStatus()
            ));
        }
    }

    private static Map<String, String> getRandomDates() {
        Map<String, String> dates = new HashMap<>();
        Random random = new Random();
        int day = random.nextInt(25 - 1) + 1;
        String strDay = (day < 10) ? "0" + day : String.valueOf(day);
        int month = random.nextInt(12 - 1) + 1;
        String strMonth = (month < 10) ? "0" + month : String.valueOf(month);
        int year = random.nextInt(2023 - 2019) + 2019;
        String creationDate = String.format("%s/%s/%d", strDay, strMonth, year);
        dates.put("creationDate", creationDate);
        year += 1;
        String dueDate = String.format("%s/%s/%d", strDay, strMonth, year);
        dates.put("dueDate", dueDate);
        return dates;
    }

    private static Map<String, String> getPersons(int id) {
        Map<String, String> persons = new HashMap<>();
        persons.put("createdBy", "Selene Pascali");
        if (id % 2 == 0) {
            persons.put("leader", "Angel Calzada");
            persons.put("client", "Prosa");
            persons.put("pm", "Salvador Gonzalez Villanueva");
        } else if (id % 3 == 0) {
            persons.put("leader", "Alvaro Mendoza");
            persons.put("client", "Prosa");
            persons.put("pm", "Lizbeth Quintana Ortega");
        } else {
            persons.put("leader", "Gerardo Lopez");
            persons.put("client", "SAS");
            persons.put("pm", "Jose Alberto De Medina Medina");
        }

        return persons;
    }

    private static int getStatus() {
        Random random = new Random();
        return random.nextInt(5 - 1) + 1;
    }

    public ProjectDummy save(ProjectDummy projectDummy) {
        if (projectDummy.getId() == null) {
            projectDummy.setId(Long.valueOf((TABLE.size() + 1)));
        }
        TABLE.add(projectDummy);
        return findById(projectDummy.getId());
    }

    public List<ProjectDummy> findAll() {
        return TABLE;
    }

    public Page<ProjectDummy> findAll(Pageable pageable) {
        Integer pageSize = pageable.getPageSize();
        List<ProjectDummy> tablePagged= new ArrayList<>(TABLE);
        if (pageSize == null || pageSize <= 0 || pageSize > tablePagged.size())
            pageSize = tablePagged.size();
        int numPages = (int) Math.ceil((double)tablePagged.size() / (double)pageSize);
        List<List<ProjectDummy>> pages = new ArrayList<>(numPages);
        for (int pageNum = 0; pageNum < numPages;)
            pages.add(tablePagged.subList(pageNum * pageSize, Math.min(++pageNum * pageSize, tablePagged.size())));
        return new PageImpl<>(pages.get(pageable.getPageNumber()), pageable, TABLE.size());

    }

    public ProjectDummy findById(Long id) {
        return TABLE.get(TABLE.indexOf(new ProjectDummy(id)));
    }
}
