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
    private Long id;
    private Date fechaRequisicion;

    @ManyToOne
    @JoinColumn(name = "id_proyecto")
    private Project project;

    @OneToMany(mappedBy = "requisicion", cascade = CascadeType.ALL)
    private List<Compra> compras;
}
