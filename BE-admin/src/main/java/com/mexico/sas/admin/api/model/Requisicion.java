package com.mexico.sas.admin.api.model;

import lombok.Data;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "tbl_requisicion")
@Data
public class Requisicion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long idRequisicion;
    private Date fechaRequisicion;
    @Column(unique = true)
    private Long id_proyecto;

    @OneToOne(mappedBy = "requisicion")
    private Project project;

    @OneToMany(mappedBy = "requisicion", cascade = CascadeType.ALL)
    private List<Compra> compras;
}
