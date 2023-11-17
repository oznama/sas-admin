-- Usuarios
INSERT INTO users (id, name, surname, email, phone, creation_date, active) VALUES (1, 'admin', 'admin', 'admin@sas-mexico.com', '555522558866', current_timestamp, 'true');
-- INSERT INTO user_security (user_id, password) VALUES (1, '12345678');
INSERT INTO user_security (user_id, password, creation_date) VALUES (1, 'Ea6SCcSRF9rJUxhtMk3bXg==', current_timestamp);

-- Catalogos

---- Maquina de estados
INSERT INTO catalog (id, value, description, type, user_id, creation_date) VALUES (1000000001, 'Catalogo de estatus', 'Maquina de estado', 2000200002, 1, current_timestamp);
INSERT INTO catalog (id, value, description, type, user_id, creation_date, catalog_parent_id) VALUES (2000100001, 'Activo', 'Registro activo', 2000200002, 1, current_timestamp, 1000000001);
INSERT INTO catalog (id, value, description, type, user_id, creation_date, catalog_parent_id) VALUES (2000100002, 'Inactivo', 'Registro inactivo', 2000200002, 1, current_timestamp, 1000000001);
INSERT INTO catalog (id, value, description, type, user_id, creation_date, catalog_parent_id) VALUES (2000100003, 'Eliminado', 'Registro eliminado', 2000200002, 1, current_timestamp, 1000000001);

---- Catalogo de tipos de catalogo
INSERT INTO catalog (id, value, description, type, user_id, creation_date) VALUES (1000000002, 'Modulos', 'Catalogo de modulos', 2000200002, 1, current_timestamp);
INSERT INTO catalog (id, value, description, type, user_id, creation_date, catalog_parent_id) VALUES (2000200001, 'Interno', 'Tipo de catalogo interno', 2001200002, 1, current_timestamp, 1000000002);
INSERT INTO catalog (id, value, description, type, user_id, creation_date, catalog_parent_id) VALUES (2000200002, 'Externo', 'Tipo de catalogo externo', 2001200002, 1, current_timestamp, 1000000002);


---- Formato fecha
INSERT INTO catalog (id, value, description, user_id, creation_date) VALUES (1000000003, 'FORMATOS FECHA', 'Formatos fecha', 1, current_timestamp);
INSERT INTO catalog (id, value, description, user_id, creation_date, catalog_parent_id) VALUES (2000300001, 'dd/MM/yy', 'dd/mm/aa', 1, current_timestamp, 1000000003);
INSERT INTO catalog (id, value, description, user_id, creation_date, catalog_parent_id) VALUES (2000300002, 'dd/MM/yyyy', 'dd/mm/aaaa', 1, current_timestamp, 1000000003);
INSERT INTO catalog (id, value, description, user_id, creation_date, catalog_parent_id) VALUES (2000300003, 'dd/MM/yyyy HH:mm:ss', 'dd/mm/aaaa hh:mm:ss', 1, current_timestamp, 1000000003);
INSERT INTO catalog (id, value, description, user_id, creation_date, catalog_parent_id) VALUES (2000300004, 'yyyy-MM-dd', 'aaaa-mm-dd', 1, current_timestamp, 1000000003);

---- URL
INSERT INTO catalog (id, value, description, user_id, creation_date) VALUES (1000000004, 'Urls', 'Catalogo de urls', 1, current_timestamp);
INSERT INTO catalog (id, value, description, user_id, creation_date, catalog_parent_id) VALUES (2000400001, 'https://localhost:8990/api/users/security/confirm-account', 'Url servicio confirmar usuario', 1, current_timestamp, 1000000004);
INSERT INTO catalog (id, value, description, user_id, creation_date, catalog_parent_id) VALUES (2000400002, 'https://localhost:5137/generate-password', 'Url front para asignar password', 1, current_timestamp, 1000000004);
INSERT INTO catalog (id, value, description, user_id, creation_date, catalog_parent_id) VALUES (2000400003, 'https://localhost:5137/error-token', 'Url front para link expirado', 1, current_timestamp, 1000000004);

---- Crear catalogo bitacora (eventos, detalle)
INSERT INTO catalog (id, value, description, user_id, creation_date) VALUES (1000000005, 'Bitacora evento', 'Catalogo de eventos de bitacora', 1, current_timestamp);
INSERT INTO catalog (id, value, description, user_id, creation_date, catalog_parent_id) VALUES (2000500001, 'Insert', 'Insert', 1, current_timestamp, 1000000005);
INSERT INTO catalog (id, value, description, user_id, creation_date, catalog_parent_id) VALUES (2000500002, 'Update', 'Update', 1, current_timestamp, 1000000005);
INSERT INTO catalog (id, value, description, user_id, creation_date, catalog_parent_id) VALUES (2000500003, 'Delete', 'Delete', 1, current_timestamp, 1000000005);

INSERT INTO catalog (id, value, description, user_id, creation_date) VALUES (1000000006, 'LOG_DETAIL', 'Catalogo de detalle de bitacora', 1, current_timestamp);
INSERT INTO catalog (id, value, description, user_id, creation_date, catalog_parent_id) VALUES (2000600001, 'Insert', 'New record', 1, current_timestamp, 1000000006);
INSERT INTO catalog (id, value, description, user_id, creation_date, catalog_parent_id) VALUES (2000600002, 'Update', 'Change data', 1, current_timestamp, 1000000006);
INSERT INTO catalog (id, value, description, user_id, creation_date, catalog_parent_id) VALUES (2000600003, 'Delete', 'Physical delete', 1, current_timestamp, 1000000006);
INSERT INTO catalog (id, value, description, user_id, creation_date, catalog_parent_id) VALUES (2000600004, 'Status', 'Status change', 1, current_timestamp, 1000000006);
INSERT INTO catalog (id, value, description, user_id, creation_date, catalog_parent_id) VALUES (2000600005, 'Eliminate', 'Logic delete', 1, current_timestamp, 1000000006);

-- Catalogo de modulos
INSERT INTO catalog(id, value, description, type, user_id, creation_date) VALUES (1000000030, 'Modulos', 'Catalogo de modulos', 2001200002, 1, current_timestamp);

-- Roles
INSERT INTO sso_role (name, description, user_id, creation_date) VALUES ('ADMIN', 'Usuario Administrador', 1, current_timestamp);

UPDATE users SET role_id = 1 WHERE id = 1;

---------------------------------------------------------------------
------------------------------- SAS ADMIN ---------------------------
---------------------------------------------------------------------

-- Usuarios
INSERT INTO users (id, name, surname, email, phone, creation_date, active, role_id) VALUES (2, 'Jaime', 'Carreno', 'jaime.careno@sas-mexico.com', '555522558866', current_timestamp, 'true', 1);
INSERT INTO users (id, name, surname, email, phone, creation_date, active, role_id) VALUES (3, 'Selene', 'Pascali', 'selene.pascali@sas-mexico.com', '555522558866', current_timestamp, 'true', 1);
INSERT INTO users (id, name, surname, email, phone, creation_date, active) VALUES (4, 'Angel', 'Calzada', 'angel.calzado@sas-mexico.com', '555522558866', current_timestamp, 'true');
INSERT INTO users (id, name, surname, email, phone, creation_date, active) VALUES (5, 'Alvaro', 'Mendoza', 'alvaro.mendoza@sas-mexico.com', '555522558866', current_timestamp, 'true');
INSERT INTO users (id, name, surname, email, phone, creation_date, active) VALUES (6, 'Gerardo', 'Lopez', 'gerardo.lopez@sas-mexico.com', '555522558866', current_timestamp, 'true');
INSERT INTO users (id, name, surname, email, phone, creation_date, active) VALUES (7, 'Oziel', 'Naranjo', 'oziel.naranjo@sas-mexico.com', '555522558866', current_timestamp, 'true');

INSERT INTO user_security (user_id, password, creation_date) VALUES (2, 'Ea6SCcSRF9rJUxhtMk3bXg==', current_timestamp);
INSERT INTO user_security (user_id, password, creation_date) VALUES (3, 'Ea6SCcSRF9rJUxhtMk3bXg==', current_timestamp);
INSERT INTO user_security (user_id, password, creation_date) VALUES (4, 'Ea6SCcSRF9rJUxhtMk3bXg==', current_timestamp);
INSERT INTO user_security (user_id, password, creation_date) VALUES (5, 'Ea6SCcSRF9rJUxhtMk3bXg==', current_timestamp);
INSERT INTO user_security (user_id, password, creation_date) VALUES (6, 'Ea6SCcSRF9rJUxhtMk3bXg==', current_timestamp);
INSERT INTO user_security (user_id, password, creation_date) VALUES (7, 'Ea6SCcSRF9rJUxhtMk3bXg==', current_timestamp);

-- Roles
INSERT INTO sso_role (id, name, description, user_id, creation_date) VALUES (2, 'DEV', 'Desarrollador', 1, current_timestamp);

UPDATE users SET role_id = 2 WHERE id = 4;
UPDATE users SET role_id = 2 WHERE id = 5;
UPDATE users SET role_id = 2 WHERE id = 6;
UPDATE users SET role_id = 2 WHERE id = 7;

-- Catalogos

---- Aplicaciones
INSERT INTO catalog (id, value, description, type, user_id, creation_date) VALUES (1000000008, 'Aplicaciones', '', 2000200002, 1, current_timestamp);
INSERT INTO catalog (id, value, description, type, user_id, creation_date, catalog_parent_id) VALUES (2000800001, 'ACT', '', 2000200002, 1, current_timestamp, 1000000008);
INSERT INTO catalog (id, value, description, type, user_id, creation_date, catalog_parent_id) VALUES (2000800002, 'CTM', '', 2000200002, 1, current_timestamp, 1000000008);
INSERT INTO catalog (id, value, description, type, user_id, creation_date, catalog_parent_id) VALUES (2000800003, 'IFO', '', 2000200002, 1, current_timestamp, 1000000008);
INSERT INTO catalog (id, value, description, type, user_id, creation_date, catalog_parent_id) VALUES (2000800004, 'PROALERTA', '', 2000200002, 1, current_timestamp, 1000000008);
INSERT INTO catalog (id, value, description, type, user_id, creation_date, catalog_parent_id) VALUES (2000800005, 'PTLF', '', 2000200002, 1, current_timestamp, 1000000008);
INSERT INTO catalog (id, value, description, type, user_id, creation_date, catalog_parent_id) VALUES (2000800006, 'PTLFH', '', 2000200002, 1, current_timestamp, 1000000008);
INSERT INTO catalog (id, value, description, type, user_id, creation_date, catalog_parent_id) VALUES (2000800007, 'PYW', '', 2000200002, 1, current_timestamp, 1000000008);
INSERT INTO catalog (id, value, description, type, user_id, creation_date, catalog_parent_id) VALUES (2000800008, 'REPORTES UPTIME', '', 2000200002, 1, current_timestamp, 1000000008);
INSERT INTO catalog (id, value, description, type, user_id, creation_date, catalog_parent_id) VALUES (2000800009, 'REPRED / RED WEB', '', 2000200002, 1, current_timestamp, 1000000008);
INSERT INTO catalog (id, value, description, type, user_id, creation_date, catalog_parent_id) VALUES (2000800010, 'SAC', '', 2000200002, 1, current_timestamp, 1000000008);
INSERT INTO catalog (id, value, description, type, user_id, creation_date, catalog_parent_id) VALUES (2000800011, 'SAC2', '', 2000200002, 1, current_timestamp, 1000000008);
INSERT INTO catalog (id, value, description, type, user_id, creation_date, catalog_parent_id) VALUES (2000800012, 'SAW', '', 2000200002, 1, current_timestamp, 1000000008);
INSERT INTO catalog (id, value, description, type, user_id, creation_date, catalog_parent_id) VALUES (2000800013, 'SCA', '', 2000200002, 1, current_timestamp, 1000000008);
INSERT INTO catalog (id, value, description, type, user_id, creation_date, catalog_parent_id) VALUES (2000800014, 'SCL', '', 2000200002, 1, current_timestamp, 1000000008);
INSERT INTO catalog (id, value, description, type, user_id, creation_date, catalog_parent_id) VALUES (2000800015, 'SIA', '', 2000200002, 1, current_timestamp, 1000000008);
INSERT INTO catalog (id, value, description, type, user_id, creation_date, catalog_parent_id) VALUES (2000800016, 'SIF', '', 2000200002, 1, current_timestamp, 1000000008);
INSERT INTO catalog (id, value, description, type, user_id, creation_date, catalog_parent_id) VALUES (2000800017, 'PMT', '', 2000200002, 1, current_timestamp, 1000000008);
INSERT INTO catalog (id, value, description, type, user_id, creation_date, catalog_parent_id) VALUES (2000800018, 'PDF 2', '', 2000200002, 1, current_timestamp, 1000000008);
INSERT INTO catalog (id, value, description, type, user_id, creation_date, catalog_parent_id) VALUES (2000800019, 'MDT', '', 2000200002, 1, current_timestamp, 1000000008);

-- Cliente
INSERT INTO clients (id, name, user_id, creation_date) VALUES (1, 'PROSA', 1, current_timestamp);
-- Empleados
INSERT INTO employees (id, name, surname, user_id, creation_date, client_id) VALUES (1, 'Rogelio', 'Zavaleta', 1, current_timestamp, 1);
INSERT INTO employees (id, name, surname, second_surname, user_id, creation_date, client_id) VALUES (2, 'Fernando', 'Chavez', 'Nava', 1, current_timestamp, 1);
INSERT INTO employees (id, name, second_name, surname, second_surname, user_id, creation_date, client_id) VALUES (3, 'Karen', 'Paulina', 'Lopez', 'Medina', 1, current_timestamp, 1);
INSERT INTO employees (id, name, second_name, surname, second_surname, user_id, creation_date, client_id) VALUES (4, 'Miguel', 'Angel', 'Poblete', 'Marroquin', 1, current_timestamp, 1);