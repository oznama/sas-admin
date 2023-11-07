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

SELECT setval('users_id_seq', 7);

-- Roles
INSERT INTO sso_role (id, name, description, user_id, creation_date) VALUES (2, 'DEV', 'Desarrollador', 1, current_timestamp);

UPDATE users SET role_id = 2 WHERE id = 4;
UPDATE users SET role_id = 2 WHERE id = 5;
UPDATE users SET role_id = 2 WHERE id = 6;
UPDATE users SET role_id = 2 WHERE id = 7;

SELECT setval('sso_role_id_seq', 2);

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

SELECT setval('clients_id_seq', 1);
-- Empleados
INSERT INTO employees (id, name, surname, user_id, creation_date, client_id) VALUES (1, 'Rogelio', 'Zavaleta', 1, current_timestamp, 1);
INSERT INTO employees (id, name, surname, second_surname, user_id, creation_date, client_id) VALUES (2, 'Fernando', 'Chavez', 'Nava', 1, current_timestamp, 1);
INSERT INTO employees (id, name, second_name, surname, second_surname, user_id, creation_date, client_id) VALUES (3, 'Karen', 'Paulina', 'Lopez', 'Medina', 1, current_timestamp, 1);
INSERT INTO employees (id, name, second_name, surname, second_surname, user_id, creation_date, client_id) VALUES (4, 'Miguel', 'Angel', 'Poblete', 'Marroquin', 1, current_timestamp, 1);

SELECT setval('employees_id_seq', 4);

-- Proyectos
INSERT INTO projects (id, p_key, description, status, client_id, project_manager_id, user_id, creation_date) VALUES (1, 'T-62-9900-22', 'Generación de reportes por módulo en SIA para MIFEL', 1, 1, 1, 3, current_timestamp);
INSERT INTO projects (id, p_key, description, status, client_id, project_manager_id, user_id, creation_date) VALUES (2, 'C-20-1809-22', 'BradesCard, Alta Del Servicio De Lote De Aclaraciones', 2, 1, 2, 3, current_timestamp);
INSERT INTO projects (id, p_key, description, status, client_id, project_manager_id, user_id, creation_date) VALUES (3, 'H-62-9865-22', 'Automatización de respaldos para el Producto de Banca', 1, 1, 1, 3, current_timestamp);
INSERT INTO projects (id, p_key, description, status, client_id, project_manager_id, user_id, creation_date) VALUES (4, 'N-77-1783-22', 'Cámara MC/Producto CC – Implementación de MasterCard como Cámara de Compensación Adquirente POS', 1, 1, 1, 3, current_timestamp);
INSERT INTO projects (id, p_key, description, status, client_id, project_manager_id, user_id, creation_date) VALUES (5, 'C-24-1853-22', 'Automatización del servicio S9005 Transacciones de Cargo Automático y envío de detalle a cliente', 4, 1, 3, 3, current_timestamp);
INSERT INTO projects (id, p_key, description, status, client_id, project_manager_id, user_id, creation_date) VALUES (6, 'A-20-1784-22', 'FIRST DATA FISERV - ALTA ADQUIRENTE BACKEND E INTEGRACIÓN DE OPERATIVAS ISO-AGREGADOR', 1, 1, 1, 3, current_timestamp);
INSERT INTO projects (id, p_key, description, status, client_id, project_manager_id, user_id, creation_date) VALUES (7, 'C-24-1653-22', 'Fase II Facturación automática Sigue mi Tarjeta', 2, 1, 4, 3, current_timestamp);
INSERT INTO projects (id, p_key, description, status, client_id, project_manager_id, user_id, creation_date) VALUES (8, 'C-20-1970-22', 'ALTA DE Bin Visa 45178000 para caja popular APASEO DEL ALTO CON FAST FUNDS', 3, 1, 3, 3, current_timestamp);

SELECT setval('projects_id_seq', 8);

-- Seguridad para servicio de Proyecto
INSERT INTO permission (id, name, description, paths_allowed, user_id, creation_date) VALUES (9, 'PROJ-NEW', 'Puede registrar proyectos', '/projects_POST', 1, current_timestamp);
INSERT INTO permission (id, name, description, paths_allowed, user_id, creation_date) VALUES (10, 'PROJ-UPD', 'Puede registrar proyectos', '/projects_PUT', 1, current_timestamp);
INSERT INTO permission (id, name, description, paths_allowed, user_id, creation_date) VALUES (11, 'PROJ-STA', 'Puede desactivar proyectos', '/projects_PATCH', 1, current_timestamp);
INSERT INTO permission (id, name, description, paths_allowed, user_id, creation_date) VALUES (12, 'PROJ-DEL', 'Puede eliminar proyectos', '/projects_DELETE', 1, current_timestamp);
INSERT INTO permission (id, name, description, paths_allowed, user_id, creation_date) VALUES (13, 'PROJ-GET', 'Puede consultar proyectos', '/projects_GET', 1, current_timestamp);

INSERT INTO sso_roles_permissions (role_id, permission_id, user_id, creation_date) VALUES (1, 9, 1, current_timestamp);
INSERT INTO sso_roles_permissions (role_id, permission_id, user_id, creation_date) VALUES (1, 10, 1, current_timestamp);
INSERT INTO sso_roles_permissions (role_id, permission_id, user_id, creation_date) VALUES (1, 11, 1, current_timestamp);
INSERT INTO sso_roles_permissions (role_id, permission_id, user_id, creation_date) VALUES (1, 12, 1, current_timestamp);
INSERT INTO sso_roles_permissions (role_id, permission_id, user_id, creation_date) VALUES (1, 13, 1, current_timestamp);

-- Seguridad para servicio de Cliente
INSERT INTO permission (id, name, description, paths_allowed, user_id, creation_date) VALUES (14, 'CLI-NEW', 'Puede registrar clientes', '/clients_POST', 1, current_timestamp);
INSERT INTO permission (id, name, description, paths_allowed, user_id, creation_date) VALUES (15, 'CLI-UPD', 'Puede registrar clientes', '/clients_PUT', 1, current_timestamp);
INSERT INTO permission (id, name, description, paths_allowed, user_id, creation_date) VALUES (16, 'CLI-STA', 'Puede desactivar clientes', '/clients_PATCH', 1, current_timestamp);
INSERT INTO permission (id, name, description, paths_allowed, user_id, creation_date) VALUES (17, 'CLI-DEL', 'Puede eliminar clientes', '/clients_DELETE', 1, current_timestamp);
INSERT INTO permission (id, name, description, paths_allowed, user_id, creation_date) VALUES (18, 'CLI-GET', 'Puede consultar clientes', '/clients_GET', 1, current_timestamp);

INSERT INTO sso_roles_permissions (role_id, permission_id, user_id, creation_date) VALUES (1, 14, 1, current_timestamp);
INSERT INTO sso_roles_permissions (role_id, permission_id, user_id, creation_date) VALUES (1, 15, 1, current_timestamp);
INSERT INTO sso_roles_permissions (role_id, permission_id, user_id, creation_date) VALUES (1, 16, 1, current_timestamp);
INSERT INTO sso_roles_permissions (role_id, permission_id, user_id, creation_date) VALUES (1, 17, 1, current_timestamp);
INSERT INTO sso_roles_permissions (role_id, permission_id, user_id, creation_date) VALUES (1, 18, 1, current_timestamp);

-- Update secuencia permission
SELECT setval('permission_id_seq', 18);