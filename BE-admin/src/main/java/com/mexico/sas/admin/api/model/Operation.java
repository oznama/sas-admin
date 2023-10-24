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
    private Long id_app_usr;
    private Long id_project;
}
