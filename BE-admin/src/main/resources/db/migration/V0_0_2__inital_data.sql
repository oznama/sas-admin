-- Compañias
INSERT INTO companies (id, name, email_domain, type) VALUES (1, 'SAS', 'sas-mexico.com', 2000900001);
INSERT INTO companies (id, name, email_domain, type) VALUES (2, 'PROSA', 'prosa.com.mx', 2000900002);

SELECT setval('companies_id_seq', 2);

-- Empleados
INSERT INTO employees (id, email, name, surname, company_id) VALUES (1, 'admin@sas-mexico.com', 'admin', 'admin', 1);
INSERT INTO employees (id, email, name, surname, second_surname, company_id, position_id) VALUES (2, 'jaime.carreno@sas-mexico.com', 'Jaime', CONCAT('Carre',E'\u00F1','o'), 'Mendez', 1, 2000500001);
INSERT INTO employees (id, email, name, surname, second_surname, company_id, boss_id, position_id) VALUES (3, 'selene.pascalis@sas-mexico.com', 'Selene', 'Pascalis', 'Garcia', 1, 2, 2000500002);
INSERT INTO employees (id, email, name, surname, company_id, boss_id, position_id) VALUES (4, 'angel.calzada@sas-mexico.com', 'Angel', 'Calzada', 1, 3, 2000500005);
INSERT INTO employees (id, email, name, surname, company_id, boss_id, position_id) VALUES (5, 'alvaro.mendoza@sas-mexico.com', 'Alvaro', 'Mendoza', 1, 3, 2000500006);
INSERT INTO employees (id, email, name, surname, second_surname, company_id, boss_id, position_id) VALUES (6, 'juan.banos@sas-mexico.com', 'Juan', CONCAT('Ba',E'\u00F1','os'), 'Soto', 1, 3, 2000500006);
INSERT INTO employees (id, email, name, surname, company_id, boss_id, position_id) VALUES (7, 'gerardo.lopez@sas-mexico.com', 'Gerardo', 'Lopez', 1, 3, 2000500006);
INSERT INTO employees (id, email, name, surname, second_surname, company_id, boss_id, position_id) VALUES (8, 'oziel.naranjo@sas-mexico.com', 'Oziel', 'Naranjo', CONCAT('M',E'\u00E1','rquez'), 1, 3, 2000500006);

SELECT setval('employees_id_seq', 8);

-- Roles
INSERT INTO sso_role (id, name, description) VALUES (1, 'ROOT', 'Usuario root');
INSERT INTO sso_role (id, name, description) VALUES (2, 'BOSS', 'Usuario Jaime');
INSERT INTO sso_role (id, name, description) VALUES (3, 'ADMIN', 'Usuario Selene');
INSERT INTO sso_role (id, name, description) VALUES (4, 'USER', 'Usuario general');

SELECT setval('sso_role_id_seq', 3);

-- Permisos
INSERT INTO permission (id, name, description) VALUES(1, 'Get-Cat', 'Ver catalogos');
INSERT INTO permission (id, name, description) VALUES(2, 'Create-Cat', 'Crear catalogos');
INSERT INTO permission (id, name, description) VALUES(3, 'Edit-Cat', 'Editar catalogos');
INSERT INTO permission (id, name, description) VALUES(4, 'Del-Cat', 'Eliminar catalogos');
INSERT INTO permission (id, name, description) VALUES(5, 'Admin-Cli', 'Administracion de companias');
INSERT INTO permission (id, name, description) VALUES(6, 'Get-Emp', 'Ver de empleados');
INSERT INTO permission (id, name, description) VALUES(7, 'Admin-Usr', 'Administracion de usuarios');
INSERT INTO permission (id, name, description) VALUES(8, 'Admin-App', 'Administracion de aplicaciones');
INSERT INTO permission (id, name, description) VALUES(9, 'Create-proj', 'Crear proyectos');
INSERT INTO permission (id, name, description) VALUES(10, 'Edit-proj', 'Editar proyectos');
INSERT INTO permission (id, name, description) VALUES(11, 'Edit-proj-pm', 'Editar el pm del proyecto');
INSERT INTO permission (id, name, description) VALUES(12, 'Dis-proj', 'Desactivar proyectos');
INSERT INTO permission (id, name, description) VALUES(13, 'Del-proj', 'Eliminar proyectos');
INSERT INTO permission (id, name, description) VALUES(14, 'Create-proj-app', 'Asociacion de aplicaciones a proyecto');
INSERT INTO permission (id, name, description) VALUES(15, 'Edit-proj-app', 'Editar detalles de aplicacion en proyecto');
INSERT INTO permission (id, name, description) VALUES(16, 'Del-proj-app', 'Eliminar asociacion de aplicacion a proyecto');
INSERT INTO permission (id, name, description) VALUES(17, 'Get-Ord', 'Ver Ordenes');
INSERT INTO permission (id, name, description) VALUES(18, 'Create-Ord', 'Crear Ordenes');
INSERT INTO permission (id, name, description) VALUES(19, 'Edit-Ord', 'Editar Ordenes');
INSERT INTO permission (id, name, description) VALUES(20, 'Del-Ord', 'Eliminar Ordenes');
INSERT INTO permission (id, name, description) VALUES(21, 'Create-Emp', 'Crear empleados');
INSERT INTO permission (id, name, description) VALUES(22, 'Edit-Emp', 'Editar empleados');
INSERT INTO permission (id, name, description) VALUES(23, 'Del-Emp', 'Eliminar empleados');
INSERT INTO permission (id, name, description) VALUES(24, 'Get-Comp', 'Ver de empresas');
INSERT INTO permission (id, name, description) VALUES(25, 'Create-Comp', 'Crear empresas');
INSERT INTO permission (id, name, description) VALUES(26, 'Edit-Comp', 'Editar empresas');
INSERT INTO permission (id, name, description) VALUES(27, 'Del-Comp', 'Eliminar empresas');


SELECT setval('permission_id_seq', 15);

-- Rol Permiso
INSERT INTO sso_roles_permissions (role_id, permission_id) VALUES(1, 1);
INSERT INTO sso_roles_permissions (role_id, permission_id) VALUES(1, 2);
INSERT INTO sso_roles_permissions (role_id, permission_id) VALUES(1, 3);
INSERT INTO sso_roles_permissions (role_id, permission_id) VALUES(1, 4);
INSERT INTO sso_roles_permissions (role_id, permission_id) VALUES(1, 5);
INSERT INTO sso_roles_permissions (role_id, permission_id) VALUES(1, 6);
INSERT INTO sso_roles_permissions (role_id, permission_id) VALUES(1, 7);
INSERT INTO sso_roles_permissions (role_id, permission_id) VALUES(1, 8);
INSERT INTO sso_roles_permissions (role_id, permission_id) VALUES(1, 9);
INSERT INTO sso_roles_permissions (role_id, permission_id) VALUES(1, 10);
INSERT INTO sso_roles_permissions (role_id, permission_id) VALUES(1, 11);
INSERT INTO sso_roles_permissions (role_id, permission_id) VALUES(1, 12);
INSERT INTO sso_roles_permissions (role_id, permission_id) VALUES(1, 13);
INSERT INTO sso_roles_permissions (role_id, permission_id) VALUES(1, 14);
INSERT INTO sso_roles_permissions (role_id, permission_id) VALUES(1, 15);
INSERT INTO sso_roles_permissions (role_id, permission_id) VALUES(1, 16);
INSERT INTO sso_roles_permissions (role_id, permission_id) VALUES(1, 17);
INSERT INTO sso_roles_permissions (role_id, permission_id) VALUES(1, 18);
INSERT INTO sso_roles_permissions (role_id, permission_id) VALUES(1, 19);
INSERT INTO sso_roles_permissions (role_id, permission_id) VALUES(1, 20);
INSERT INTO sso_roles_permissions (role_id, permission_id) VALUES(1, 21);
INSERT INTO sso_roles_permissions (role_id, permission_id) VALUES(1, 22);
INSERT INTO sso_roles_permissions (role_id, permission_id) VALUES(1, 23);
INSERT INTO sso_roles_permissions (role_id, permission_id) VALUES(1, 24);
INSERT INTO sso_roles_permissions (role_id, permission_id) VALUES(1, 25);
INSERT INTO sso_roles_permissions (role_id, permission_id) VALUES(1, 26);
INSERT INTO sso_roles_permissions (role_id, permission_id) VALUES(1, 27);

INSERT INTO sso_roles_permissions (role_id, permission_id) VALUES(2, 1);
INSERT INTO sso_roles_permissions (role_id, permission_id) VALUES(2, 2);
INSERT INTO sso_roles_permissions (role_id, permission_id) VALUES(2, 3);
INSERT INTO sso_roles_permissions (role_id, permission_id) VALUES(2, 4);
INSERT INTO sso_roles_permissions (role_id, permission_id) VALUES(2, 5);
INSERT INTO sso_roles_permissions (role_id, permission_id) VALUES(2, 6);
INSERT INTO sso_roles_permissions (role_id, permission_id) VALUES(2, 7);
INSERT INTO sso_roles_permissions (role_id, permission_id) VALUES(2, 8);
INSERT INTO sso_roles_permissions (role_id, permission_id) VALUES(2, 9);
INSERT INTO sso_roles_permissions (role_id, permission_id) VALUES(2, 10);
INSERT INTO sso_roles_permissions (role_id, permission_id) VALUES(2, 11);
INSERT INTO sso_roles_permissions (role_id, permission_id) VALUES(2, 12);
INSERT INTO sso_roles_permissions (role_id, permission_id) VALUES(2, 13);
INSERT INTO sso_roles_permissions (role_id, permission_id) VALUES(2, 14);
INSERT INTO sso_roles_permissions (role_id, permission_id) VALUES(2, 15);
INSERT INTO sso_roles_permissions (role_id, permission_id) VALUES(2, 16);
INSERT INTO sso_roles_permissions (role_id, permission_id) VALUES(2, 17);
INSERT INTO sso_roles_permissions (role_id, permission_id) VALUES(2, 20);
INSERT INTO sso_roles_permissions (role_id, permission_id) VALUES(2, 21);
INSERT INTO sso_roles_permissions (role_id, permission_id) VALUES(2, 22);
INSERT INTO sso_roles_permissions (role_id, permission_id) VALUES(2, 23);
INSERT INTO sso_roles_permissions (role_id, permission_id) VALUES(2, 24);
INSERT INTO sso_roles_permissions (role_id, permission_id) VALUES(2, 25);
INSERT INTO sso_roles_permissions (role_id, permission_id) VALUES(2, 26);
INSERT INTO sso_roles_permissions (role_id, permission_id) VALUES(2, 27);

INSERT INTO sso_roles_permissions (role_id, permission_id) VALUES(3, 2);
INSERT INTO sso_roles_permissions (role_id, permission_id) VALUES(3, 6);
INSERT INTO sso_roles_permissions (role_id, permission_id) VALUES(3, 7);
INSERT INTO sso_roles_permissions (role_id, permission_id) VALUES(3, 8);
INSERT INTO sso_roles_permissions (role_id, permission_id) VALUES(3, 11);
INSERT INTO sso_roles_permissions (role_id, permission_id) VALUES(3, 17);
INSERT INTO sso_roles_permissions (role_id, permission_id) VALUES(3, 18);
INSERT INTO sso_roles_permissions (role_id, permission_id) VALUES(3, 19);
INSERT INTO sso_roles_permissions (role_id, permission_id) VALUES(3, 20);
INSERT INTO sso_roles_permissions (role_id, permission_id) VALUES(3, 24);


-- Usuarios
INSERT INTO users (id, password, employee_id) VALUES (1, 'Ea6SCcSRF9rJUxhtMk3bXg==', 1);
INSERT INTO users (id, password, employee_id) VALUES (2, 'Ea6SCcSRF9rJUxhtMk3bXg==', 2);
INSERT INTO users (id, password, employee_id) VALUES (3, 'Ea6SCcSRF9rJUxhtMk3bXg==', 3);
INSERT INTO users (id, password, employee_id) VALUES (4, 'Ea6SCcSRF9rJUxhtMk3bXg==', 8);

SELECT setval('users_id_seq', 4);

UPDATE users SET role_id = 1 WHERE id = 1;
UPDATE users SET role_id = 2 WHERE id = 2;
UPDATE users SET role_id = 3 WHERE id = 3;
UPDATE users SET role_id = 4 WHERE id = 4;
UPDATE users SET role_id = 3 WHERE id = 5;
UPDATE users SET role_id = 4 WHERE id = 6;

-- Catalogos

---- Maquina de estados
INSERT INTO catalog (id, value, description, internal) VALUES (1000000001, 'Catalogo de estatus', 'Maquina de estado', true);
INSERT INTO catalog (id, value, description, catalog_parent_id) VALUES (2000100001, 'Activo', 'Registro activo', 1000000001);
INSERT INTO catalog (id, value, description, catalog_parent_id) VALUES (2000100002, 'Inactivo', 'Registro inactivo', 1000000001);
INSERT INTO catalog (id, value, description, catalog_parent_id) VALUES (2000100003, 'Eliminado', 'Registro eliminado', 1000000001);

-- Estatus Proyecto
INSERT INTO catalog (id, value, description) VALUES (1000000002, 'Estatus proyecto', 'Estados del proyecto');
INSERT INTO catalog (id, value, description, catalog_parent_id) VALUES (2000200001, 'Nuevo', 'Nuevo', 1000000002);

---- Crear catalogo bitacora
INSERT INTO catalog (id, value, description, internal) VALUES (1000000003, 'Bitacora detalle', 'Catalogo de detalle de bitacora', true);
INSERT INTO catalog (id, value, description, catalog_parent_id) VALUES (2000300001, 'Insert', 'New record', 1000000003);
INSERT INTO catalog (id, value, description, catalog_parent_id) VALUES (2000300002, 'Update', 'Change data', 1000000003);
INSERT INTO catalog (id, value, description, catalog_parent_id) VALUES (2000300003, 'Delete', 'Physical delete', 1000000003);
INSERT INTO catalog (id, value, description, catalog_parent_id) VALUES (2000300004, 'Status', 'Status change', 1000000003);
INSERT INTO catalog (id, value, description, catalog_parent_id) VALUES (2000300005, 'Eliminate', 'Logic delete', 1000000003);

---- Aplicaciones
INSERT INTO catalog (id, value) VALUES (1000000004, 'Aplicaciones');
--INSERT INTO catalog (id, value, catalog_parent_id) VALUES (2000400001, 'ACT', 1000000004);
--INSERT INTO catalog (id, value, description, type, catalog_parent_id) VALUES (2000500002, 'CTM', '', 2000200002, 1000000005);
--INSERT INTO catalog (id, value, description, type, catalog_parent_id) VALUES (2000500003, 'IFO', '', 2000200002, 1000000005);
--INSERT INTO catalog (id, value, description, type, catalog_parent_id) VALUES (2000500004, 'PROALERTA', '', 2000200002, 1000000005);
--INSERT INTO catalog (id, value, description, type, catalog_parent_id) VALUES (2000500005, 'PTLF', '', 2000200002, 1000000005);
--INSERT INTO catalog (id, value, description, type, catalog_parent_id) VALUES (2000500006, 'PTLFH', '', 2000200002, 1000000005);
--INSERT INTO catalog (id, value, description, type, catalog_parent_id) VALUES (2000500007, 'PYW', '', 2000200002, 1000000005);
--INSERT INTO catalog (id, value, description, type, catalog_parent_id) VALUES (2000500008, 'REPORTES UPTIME', '', 2000200002, 1000000005);
--INSERT INTO catalog (id, value, description, type, catalog_parent_id) VALUES (2000500009, 'REPRED / RED WEB', '', 2000200002, 1000000005);
--INSERT INTO catalog (id, value, description, type, catalog_parent_id) VALUES (2000500010, 'SAC', '', 2000200002, 1000000005);
--INSERT INTO catalog (id, value, description, type, catalog_parent_id) VALUES (2000500011, 'SAC2', '', 2000200002, 1000000005);
--INSERT INTO catalog (id, value, description, type, catalog_parent_id) VALUES (2000500012, 'SAW', '', 2000200002, 1000000005);
--INSERT INTO catalog (id, value, description, type, catalog_parent_id) VALUES (2000500013, 'SCA', '', 2000200002, 1000000005);
--INSERT INTO catalog (id, value, description, type, catalog_parent_id) VALUES (2000500014, 'SCL', '', 2000200002, 1000000005);
--INSERT INTO catalog (id, value, description, type, catalog_parent_id) VALUES (2000500015, 'SIA', '', 2000200002, 1000000005);
--INSERT INTO catalog (id, value, description, type, catalog_parent_id) VALUES (2000500016, 'SIF', '', 2000200002, 1000000005);
--INSERT INTO catalog (id, value, description, type, catalog_parent_id) VALUES (2000500017, 'PMT', '', 2000200002, 1000000005);
--INSERT INTO catalog (id, value, description, type, catalog_parent_id) VALUES (2000500018, 'PDF 2', '', 2000200002, 1000000005);
--INSERT INTO catalog (id, value, description, type, catalog_parent_id) VALUES (2000500019, 'MDT', '', 2000200002, 1000000005);

-- Puesto de trabajo
INSERT INTO catalog (id, value, description) VALUES (1000000005, 'Pruesto trabajo', 'Jerarquia laboral');
INSERT INTO catalog (id, value, catalog_parent_id) VALUES (2000500001, 'Director', 1000000005);
INSERT INTO catalog (id, value, catalog_parent_id) VALUES (2000500002, 'Subdirector', 1000000005);
INSERT INTO catalog (id, value, catalog_parent_id) VALUES (2000500003, 'Gerente', 1000000005);
INSERT INTO catalog (id, value, catalog_parent_id) VALUES (2000500004, 'PM', 1000000005);
INSERT INTO catalog (id, value, catalog_parent_id) VALUES (2000500005, 'Lider', 1000000005);
INSERT INTO catalog (id, value, catalog_parent_id) VALUES (2000500006, 'Desarrollador', 1000000005);
INSERT INTO catalog (id, value, catalog_parent_id) VALUES (2000500007, 'Analista', 1000000005);

-- Estatus ordenes
INSERT INTO catalog (id, value, description) VALUES (1000000006, 'Estatus de ordenes', 'Estatus de ordenes');
INSERT INTO catalog (id, value, catalog_parent_id) VALUES (2000600001, 'Proceso', 1000000006);
INSERT INTO catalog (id, value, catalog_parent_id) VALUES (2000600002, 'Pagada', 1000000006);
INSERT INTO catalog (id, value, catalog_parent_id) VALUES (2000600003, 'Cancelada', 1000000006);
INSERT INTO catalog (id, value, catalog_parent_id) VALUES (2000600004, 'Vencida', 1000000006);

-- Dias Feriados
INSERT INTO catalog (id, value, description) VALUES (1000000007, 'Dias feriados', 'Dias feriados');

-- Estatus facturas
INSERT INTO catalog (id, value, description) VALUES (1000000008, 'Estatus de facturas', 'Estatus de facturas');
INSERT INTO catalog (id, value, catalog_parent_id) VALUES (2000800001, 'Proceso', 1000000008);
INSERT INTO catalog (id, value, catalog_parent_id) VALUES (2000800002, 'Pagada', 1000000008);
INSERT INTO catalog (id, value, catalog_parent_id) VALUES (2000800003, 'Cancelada', 1000000008);

-- Tipos de companias
INSERT INTO catalog (id, value, description) VALUES (1000000009, CONCAT('Tipo de compa',E'\u00F1','ia'), CONCAT('Tipo de compa',E'\u00F1','ia'));
INSERT INTO catalog (id, value, catalog_parent_id) VALUES (2000900001, 'Interna', 1000000009);
INSERT INTO catalog (id, value, catalog_parent_id) VALUES (2000900002, 'Cliente', 1000000009);
INSERT INTO catalog (id, value, catalog_parent_id) VALUES (2000900003, 'Proveedor', 1000000009);

-- Proyectos
--INSERT INTO projects (id, p_key, description, status, client_id, project_manager_id) VALUES (1, 'T-62-9900-22', 'Generación de reportes por módulo en SIA para MIFEL', 1, 1, 1, 3, current_timestamp);
--INSERT INTO projects (id, p_key, description, status, client_id, project_manager_id) VALUES (2, 'C-20-1809-22', 'BradesCard, Alta Del Servicio De Lote De Aclaraciones', 2, 1, 2, 3, current_timestamp);
--INSERT INTO projects (id, p_key, description, status, client_id, project_manager_id) VALUES (3, 'H-62-9865-22', 'Automatización de respaldos para el Producto de Banca', 1, 1, 1, 3, current_timestamp);
--INSERT INTO projects (id, p_key, description, status, client_id, project_manager_id) VALUES (4, 'N-77-1783-22', 'Cámara MC/Producto CC – Implementación de MasterCard como Cámara de Compensación Adquirente POS', 1, 1, 1, 3, current_timestamp);
--INSERT INTO projects (id, p_key, description, status, client_id, project_manager_id) VALUES (5, 'C-24-1853-22', 'Automatización del servicio S9005 Transacciones de Cargo Automático y envío de detalle a cliente', 4, 1, 3, 3, current_timestamp);
--INSERT INTO projects (id, p_key, description, status, client_id, project_manager_id) VALUES (6, 'A-20-1784-22', 'FIRST DATA FISERV - ALTA ADQUIRENTE BACKEND E INTEGRACIÓN DE OPERATIVAS ISO-AGREGADOR', 1, 1, 1, 3, current_timestamp);
--INSERT INTO projects (id, p_key, description, status, client_id, project_manager_id) VALUES (7, 'C-24-1653-22', 'Fase II Facturación automática Sigue mi Tarjeta', 2, 1, 4, 3, current_timestamp);
--INSERT INTO projects (id, p_key, description, status, client_id, project_manager_id) VALUES (8, 'C-20-1970-22', 'ALTA DE Bin Visa 45178000 para caja popular APASEO DEL ALTO CON FAST FUNDS', 3, 1, 3, 3, current_timestamp);
-- Aplicaciones en proyecto
--INSERT INTO project_applications (project_id, application_id, amount, leader_id, developer_id, hours, design_date, development_date, end_date) VALUES(1, 2000500015, 5.00, 4, 4, 33, '2022-12-12 00:00:00.0', '2022-12-30 00:00:00.0', '2022-12-30 00:00:00.0', 2, current_timestamp);
--INSERT INTO project_applications (project_id, application_id, amount, leader_id, developer_id, hours, design_date, development_date, end_date) VALUES(8, 2000500017, 496.88, 5, 5, 40, '2023-01-30 00:00:00.0', '2023-02-15 00:00:00.0', '2023-02-15 00:00:00.0', 2, current_timestamp);
--INSERT INTO project_applications (project_id, application_id, amount, leader_id, developer_id, hours, design_date, development_date, end_date) VALUES(8, 2000500002, 1568.82, 6, 6, 24, '2023-01-12 00:00:00.0', '2023-02-17 00:00:00.0', '2023-02-17 00:00:00.0', 2, current_timestamp);

--SELECT setval('projects_id_seq', 8);