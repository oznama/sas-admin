-- Usuarios
INSERT INTO users (id, name, surname, email, phone, creation_date, active, role_id) VALUES (2, 'Jaime', 'Carreno', 'jaime.careno@sas-mexico.com', '555522558866', current_timestamp, 'true', 2);
INSERT INTO users (id, name, surname, email, phone, creation_date, active, role_id) VALUES (3, 'Selene', 'Pascali', 'selene.pascali@sas-mexico.com', '555522558866', current_timestamp, 'true', 2);
INSERT INTO users (id, name, surname, email, phone, creation_date, active) VALUES (4, 'Angel', 'Calzada', 'angel.calzado@sas-mexico.com', '555522558866', current_timestamp, 'true');
INSERT INTO users (id, name, surname, email, phone, creation_date, active) VALUES (5, 'Alvaro', 'Mendoza', 'alvaro.mendoza@sas-mexico.com', '555522558866', current_timestamp, 'true');
INSERT INTO users (id, name, surname, email, phone, creation_date, active) VALUES (6, 'Gerardo', 'Lopez', 'gerardo.lopez@sas-mexico.com', '555522558866', current_timestamp, 'true');
INSERT INTO users (id, name, surname, email, phone, creation_date, active) VALUES (7, 'Oziel', 'Naranjo', 'oziel.naranjo@sas-mexico.com', '555522558866', current_timestamp, 'true');

INSERT INTO user_security (user_id, password) VALUES (2, 'Ea6SCcSRF9rJUxhtMk3bXg==');
INSERT INTO user_security (user_id, password) VALUES (3, 'Ea6SCcSRF9rJUxhtMk3bXg==');
INSERT INTO user_security (user_id, password) VALUES (4, 'Ea6SCcSRF9rJUxhtMk3bXg==');
INSERT INTO user_security (user_id, password) VALUES (5, 'Ea6SCcSRF9rJUxhtMk3bXg==');
INSERT INTO user_security (user_id, password) VALUES (6, 'Ea6SCcSRF9rJUxhtMk3bXg==');
INSERT INTO user_security (user_id, password) VALUES (7, 'Ea6SCcSRF9rJUxhtMk3bXg==');

SELECT setval('users_id_seq', 7);

UPDATE users SET role_id = 3 WHERE id = 4;
UPDATE users SET role_id = 3 WHERE id = 5;
UPDATE users SET role_id = 3 WHERE id = 6;
UPDATE users SET role_id = 3 WHERE id = 7;

SELECT setval('sso_role_id_seq', 2);

---- Aplicaciones
INSERT INTO catalog (id, value, description, type, user_id, creation_date) VALUES (1000000005, 'Aplicaciones', '', 2000200002, 1, current_timestamp);
INSERT INTO catalog (id, value, description, type, user_id, creation_date, catalog_parent_id) VALUES (2000500001, 'ACT', '', 2000200002, 1, current_timestamp, 1000000005);
--INSERT INTO catalog (id, value, description, type, user_id, creation_date, catalog_parent_id) VALUES (2000500002, 'CTM', '', 2000200002, 1, current_timestamp, 1000000005);
--INSERT INTO catalog (id, value, description, type, user_id, creation_date, catalog_parent_id) VALUES (2000500003, 'IFO', '', 2000200002, 1, current_timestamp, 1000000005);
--INSERT INTO catalog (id, value, description, type, user_id, creation_date, catalog_parent_id) VALUES (2000500004, 'PROALERTA', '', 2000200002, 1, current_timestamp, 1000000005);
--INSERT INTO catalog (id, value, description, type, user_id, creation_date, catalog_parent_id) VALUES (2000500005, 'PTLF', '', 2000200002, 1, current_timestamp, 1000000005);
--INSERT INTO catalog (id, value, description, type, user_id, creation_date, catalog_parent_id) VALUES (2000500006, 'PTLFH', '', 2000200002, 1, current_timestamp, 1000000005);
--INSERT INTO catalog (id, value, description, type, user_id, creation_date, catalog_parent_id) VALUES (2000500007, 'PYW', '', 2000200002, 1, current_timestamp, 1000000005);
--INSERT INTO catalog (id, value, description, type, user_id, creation_date, catalog_parent_id) VALUES (2000500008, 'REPORTES UPTIME', '', 2000200002, 1, current_timestamp, 1000000005);
--INSERT INTO catalog (id, value, description, type, user_id, creation_date, catalog_parent_id) VALUES (2000500009, 'REPRED / RED WEB', '', 2000200002, 1, current_timestamp, 1000000005);
--INSERT INTO catalog (id, value, description, type, user_id, creation_date, catalog_parent_id) VALUES (2000500010, 'SAC', '', 2000200002, 1, current_timestamp, 1000000005);
--INSERT INTO catalog (id, value, description, type, user_id, creation_date, catalog_parent_id) VALUES (2000500011, 'SAC2', '', 2000200002, 1, current_timestamp, 1000000005);
--INSERT INTO catalog (id, value, description, type, user_id, creation_date, catalog_parent_id) VALUES (2000500012, 'SAW', '', 2000200002, 1, current_timestamp, 1000000005);
--INSERT INTO catalog (id, value, description, type, user_id, creation_date, catalog_parent_id) VALUES (2000500013, 'SCA', '', 2000200002, 1, current_timestamp, 1000000005);
--INSERT INTO catalog (id, value, description, type, user_id, creation_date, catalog_parent_id) VALUES (2000500014, 'SCL', '', 2000200002, 1, current_timestamp, 1000000005);
--INSERT INTO catalog (id, value, description, type, user_id, creation_date, catalog_parent_id) VALUES (2000500015, 'SIA', '', 2000200002, 1, current_timestamp, 1000000005);
--INSERT INTO catalog (id, value, description, type, user_id, creation_date, catalog_parent_id) VALUES (2000500016, 'SIF', '', 2000200002, 1, current_timestamp, 1000000005);
--INSERT INTO catalog (id, value, description, type, user_id, creation_date, catalog_parent_id) VALUES (2000500017, 'PMT', '', 2000200002, 1, current_timestamp, 1000000005);
--INSERT INTO catalog (id, value, description, type, user_id, creation_date, catalog_parent_id) VALUES (2000500018, 'PDF 2', '', 2000200002, 1, current_timestamp, 1000000005);
--INSERT INTO catalog (id, value, description, type, user_id, creation_date, catalog_parent_id) VALUES (2000500019, 'MDT', '', 2000200002, 1, current_timestamp, 1000000005);

-- Estatus Proyecyo-Aplicacion
--INSERT INTO catalog (id, value, description, type, user_id, creation_date) VALUES (1000000006, 'Catalogo aplicacion', 'Estados del flujo de una aplicacion en proyecto', 2000200002, 1, current_timestamp);
--INSERT INTO catalog (id, value, description, type, user_id, creation_date, catalog_parent_id) VALUES (2000600001, 'Nuevo', 'Nuevo', 2000200002, 1, current_timestamp, 1000000006);

-- Cliente
INSERT INTO clients (id, name, user_id, creation_date) VALUES (1, 'PROSA', 1, current_timestamp);

SELECT setval('clients_id_seq', 1);
-- Empleados
INSERT INTO employees (id, name, surname, user_id, creation_date, client_id) VALUES (1, 'Rogelio', 'Zavaleta', 1, current_timestamp, 1);
INSERT INTO employees (id, name, surname, second_surname, user_id, creation_date, client_id) VALUES (2, 'Fernando', 'Chavez', 'Nava', 1, current_timestamp, 1);
INSERT INTO employees (id, name, second_name, surname, second_surname, user_id, creation_date, client_id) VALUES (3, 'Karen', 'Paulina', 'Lopez', 'Medina', 1, current_timestamp, 1);
INSERT INTO employees (id, name, second_name, surname, second_surname, user_id, creation_date, client_id) VALUES (4, 'Miguel', 'Angel', 'Poblete', 'Marroquin', 1, current_timestamp, 1);

SELECT setval('employees_id_seq', 4);

-- Proyectos
--INSERT INTO projects (id, p_key, description, status, client_id, project_manager_id, user_id, creation_date) VALUES (1, 'T-62-9900-22', 'Generación de reportes por módulo en SIA para MIFEL', 1, 1, 1, 3, current_timestamp);
--INSERT INTO projects (id, p_key, description, status, client_id, project_manager_id, user_id, creation_date) VALUES (2, 'C-20-1809-22', 'BradesCard, Alta Del Servicio De Lote De Aclaraciones', 2, 1, 2, 3, current_timestamp);
--INSERT INTO projects (id, p_key, description, status, client_id, project_manager_id, user_id, creation_date) VALUES (3, 'H-62-9865-22', 'Automatización de respaldos para el Producto de Banca', 1, 1, 1, 3, current_timestamp);
--INSERT INTO projects (id, p_key, description, status, client_id, project_manager_id, user_id, creation_date) VALUES (4, 'N-77-1783-22', 'Cámara MC/Producto CC – Implementación de MasterCard como Cámara de Compensación Adquirente POS', 1, 1, 1, 3, current_timestamp);
--INSERT INTO projects (id, p_key, description, status, client_id, project_manager_id, user_id, creation_date) VALUES (5, 'C-24-1853-22', 'Automatización del servicio S9005 Transacciones de Cargo Automático y envío de detalle a cliente', 4, 1, 3, 3, current_timestamp);
--INSERT INTO projects (id, p_key, description, status, client_id, project_manager_id, user_id, creation_date) VALUES (6, 'A-20-1784-22', 'FIRST DATA FISERV - ALTA ADQUIRENTE BACKEND E INTEGRACIÓN DE OPERATIVAS ISO-AGREGADOR', 1, 1, 1, 3, current_timestamp);
--INSERT INTO projects (id, p_key, description, status, client_id, project_manager_id, user_id, creation_date) VALUES (7, 'C-24-1653-22', 'Fase II Facturación automática Sigue mi Tarjeta', 2, 1, 4, 3, current_timestamp);
--INSERT INTO projects (id, p_key, description, status, client_id, project_manager_id, user_id, creation_date) VALUES (8, 'C-20-1970-22', 'ALTA DE Bin Visa 45178000 para caja popular APASEO DEL ALTO CON FAST FUNDS', 3, 1, 3, 3, current_timestamp);
-- Aplicaciones en proyecto
--INSERT INTO project_applications (project_id, application_id, amount, leader_id, developer_id, hours, design_date, development_date, end_date, user_id, creation_date) VALUES(1, 2000500015, 5.00, 4, 4, 33, '2022-12-12 00:00:00.0', '2022-12-30 00:00:00.0', '2022-12-30 00:00:00.0', 2, current_timestamp);
--INSERT INTO project_applications (project_id, application_id, amount, leader_id, developer_id, hours, design_date, development_date, end_date, user_id, creation_date) VALUES(8, 2000500017, 496.88, 5, 5, 40, '2023-01-30 00:00:00.0', '2023-02-15 00:00:00.0', '2023-02-15 00:00:00.0', 2, current_timestamp);
--INSERT INTO project_applications (project_id, application_id, amount, leader_id, developer_id, hours, design_date, development_date, end_date, user_id, creation_date) VALUES(8, 2000500002, 1568.82, 6, 6, 24, '2023-01-12 00:00:00.0', '2023-02-17 00:00:00.0', '2023-02-17 00:00:00.0', 2, current_timestamp);

--SELECT setval('projects_id_seq', 8);