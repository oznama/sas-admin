CREATE TABLE tbl_requisicion(
    id bigserial,
    fecha_requisicion timestamp,
    id_proyecto INT8 unique,
    PRIMARY KEY (id)
);

CREATE TABLE tbl_compra(
    id bigserial,
    fecha_ord_compra timestamp,
    fecha_instalacion timestamp,
    porcentaje INT,
    factura VARCHAR(255),
    fecha_factura timestamp,
    fecha_pago timestamp,
    id_requisicion INT8,
    importe numeric(12,2),
    iva numeric(11,2),
    total numeric(12,2),
    observaciones varchar(255),
    imp_faltante numeric(12,2),
    PRIMARY KEY (id)
);

ALTER TABLE tbl_requisicion ADD CONSTRAINT fk_requisicion_proyecto
FOREIGN KEY (id_proyecto) REFERENCES tbl_project;

ALTER TABLE tbl_compra ADD CONSTRAINT fk_compra_requisicion
FOREIGN KEY (id_requisicion) REFERENCES tbl_requisicion;

ALTER TABLE tbl_project ADD COLUMN horas_trabajo INT;
ALTER TABLE tbl_project ADD COLUMN fecha_inicio timestamp;
ALTER TABLE tbl_project ADD COLUMN fecha_analisis timestamp;
ALTER TABLE tbl_project ADD COLUMN fecha_construccion timestamp;
ALTER TABLE tbl_project ADD COLUMN fecha_factura timestamp;
ALTER TABLE tbl_project ADD COLUMN fecha_instalacion timestamp;


