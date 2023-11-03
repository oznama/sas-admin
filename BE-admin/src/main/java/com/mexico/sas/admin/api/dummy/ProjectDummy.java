package com.mexico.sas.admin.api.dummy;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ProjectDummy {

    @EqualsAndHashCode.Include
    private Long id;
    private String clave;
    private String name;
    private String description;
    private String creationDate;
    private String dueDate;
    private String createdBy;
    private String leader;
    private String client;
    private String pm;
    private int status;

    public ProjectDummy(Long id) {
        this.id = id;
    }
}
