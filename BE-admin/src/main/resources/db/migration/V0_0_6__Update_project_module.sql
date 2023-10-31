CREATE TABLE tbl_compra(
    ordenCompra bigserial,
    fechaOrdCompra DATE,
    fechaInstalacion DATE,
    porcentaje INT,
    factura VARCHAR(255),
    fechaFactura DATE,
    fechaPago DATE,
    idRequisicion bigserial,
    importe numeric(12,2),
    iva numeric(11,2),
    total numeric(12,2),
    observaciones varchar(255),
    impFaltante numeric(12,2),
    PRIMARY KEY (ordenCompra),
    FOREIGN KEY (idRequisicion) REFERENCES tbl_requisicion(idRequisicion)
);

CREATE TABLE tbl_requisicion(
    idRequisicion bigserial,
    fechaRequisicion DATE,
    id_proyecto bigserial unique,
    PRIMARY KEY (requisicion),
    FOREIGN KEY (id_proyecto) REFERENCES tbl_project(id)
);

ALTER TABLE tbl_project ADD COLUMN HorasTrabajo INT;
ALTER TABLE tbl_project ADD COLUMN FechaInicio DATE;
ALTER TABLE tbl_project ADD COLUMN FechaAnalisis DATE;
ALTER TABLE tbl_project ADD COLUMN FechaConstruccion DATE;
ALTER TABLE tbl_project ADD COLUMN FechaCierre DATE;
ALTER TABLE tbl_project ADD COLUMN FechaInstalacion DATE;


