package com.mexico.sas.admin.api.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "tbl_app_usr")
@Data
public class AppUsr {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_aplicacion", nullable = false)
    private Application application;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private User user;
}
