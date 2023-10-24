package com.mexico.sas.admin.api.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "tbl_application")
@Data
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long id;
    @Column(length = 255)
    private String name;
}

