CREATE TABLE tbl_application (
    id bigserial,
    name VARCHAR(255) not null,
    PRIMARY KEY (id)
);

CREATE TABLE tbl_app_usr (
    id bigserial,
    id_aplicacion INT8,
    id_usuario INT8,
    PRIMARY KEY (id),
    FOREIGN KEY (id_aplicacion) REFERENCES tbl_application(id),
    FOREIGN KEY (id_usuario) REFERENCES users(id)
);

CREATE TABLE tbl_operation (
    id bigserial,
    id_app_usr INT8,
    id_project INT8,
    PRIMARY KEY (id),
    FOREIGN KEY (id_app_usr) REFERENCES tbl_app_usr(id),
    FOREIGN KEY (id_project) REFERENCES tbl_project(id)
);

