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
    private Long id_aplicacion;
    private Long id_usuario;
}
