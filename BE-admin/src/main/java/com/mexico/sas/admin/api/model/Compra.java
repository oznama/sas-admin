package com.mexico.sas.admin.api.model;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "tbl_compra")
@Data
public class Compra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long id;
    private Date fechaOrdCompra;
    private Date fechaInstalacion;
    private Integer porcentaje;
    @Column(length = 255)
    private String factura;
    private Date fechaFactura;
    private Date fechaPago;
    private BigDecimal importe;
    private BigDecimal iva;
    private BigDecimal total;
    @Column(length = 255)
    private String observaciones;
    private BigDecimal impFaltante;

    @ManyToOne
    @JoinColumn(name = "id_requisicion")
    private Requisicion requisicion;
}
