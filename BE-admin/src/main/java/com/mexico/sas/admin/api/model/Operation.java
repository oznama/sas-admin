package com.mexico.sas.admin.api.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "tbl_operation")
@Data
public class Operation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_app_usr", nullable = false)
    private AppUsr appUsr;

    @ManyToOne
    @JoinColumn(name = "id_project", nullable = false)
    private Project project;
}
