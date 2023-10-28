package com.mexico.sas.admin.api.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "tbl_project")
@Data
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long id;
    @Column(length = 15, unique = true)
    private String clave;
    @Column(length = 255)
    private String name;
    private Integer HorasTrabajo;
    private Date FechaInicio;
    private Date FechaAnalisis;
    private Date FechaConstruccion;
    private Date fechaFactura;
    private Date FechaInstalacion;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_requisicion", referencedColumnName = "idRequisicion")
    private Requisicion requisicion;
}
