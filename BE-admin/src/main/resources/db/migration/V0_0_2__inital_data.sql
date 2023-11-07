-- Usuarios
INSERT INTO users (id, name, surname, email, phone, creation_date, active) VALUES (1, 'admin', 'admin', 'admin@sas-mexico.com', '555522558866', current_timestamp, 'true');
INSERT INTO user_security (user_id, password, creation_date) VALUES (1, 'Ea6SCcSRF9rJUxhtMk3bXg==', current_timestamp);
SELECT setval('users_id_seq', 1);

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
INSERT INTO catalog(id, value, description, type, user_id, creation_date) VALUES (1000000007, 'Modulos', 'Catalogo de modulos', 2001200002, 1, current_timestamp);

-- Permisos
INSERT INTO permission (id, name, description, visible, paths_allowed, user_id, creation_date) VALUES (1, 'SPECIAL', 'Permiso especial', 'false', '/role,/logs', 1, current_timestamp);
INSERT INTO permission (id, name, description, paths_allowed, user_id, creation_date) VALUES (2, 'USR_NEW_EDIT', 'Permiso para crear/editar usuarios', '/users_POST,/users_PUT,/users_PATCH,/users_GET', 1, current_timestamp);
INSERT INTO permission (id, name, description, paths_allowed, user_id, creation_date) VALUES (3, 'USR_DEL', 'Permiso para eliminar usuarios', '/user_DELETE', 1, current_timestamp);
INSERT INTO permission (id, name, description, paths_allowed, user_id, creation_date) VALUES (4, 'CAT-NEW', CONCAT('Puede registrar nuevos campos en cat',E'\u00E1','logos'), '/internal_POST', 1, current_timestamp);
INSERT INTO permission (id, name, description, paths_allowed, user_id, creation_date) VALUES (5, 'CAT-UPD', CONCAT('Puede modificar campos en cat',E'\u00E1','logos'), '/internal_PUT', 1, current_timestamp);
INSERT INTO permission (id, name, description, paths_allowed, user_id, creation_date) VALUES (6, 'CAT-STA', CONCAT('Puede activar cat',E'\u00E1','logos'), '/internal_PATCH', 1, current_timestamp);
INSERT INTO permission (id, name, description, paths_allowed, user_id, creation_date) VALUES (7, 'CAT-DEL', CONCAT('Puede eliminar registros de cat',E'\u00E1','logos'), '/internal_DELETE', 1, current_timestamp);
INSERT INTO permission (id, name, description, paths_allowed, user_id, creation_date) VALUES (8, 'CAT-GET', CONCAT('Puede consultar cat',E'\u00E1','logos'), '/internal_GET', 1, current_timestamp);
-- Roles
INSERT INTO sso_role (id, name, description, user_id, creation_date) VALUES (1, 'ADMIN', 'Usuario Administrador', 1, current_timestamp);
-- Roles-Permisos
INSERT INTO sso_roles_permissions (role_id, permission_id, user_id, creation_date) VALUES (1, 1, 1, current_timestamp);
INSERT INTO sso_roles_permissions (role_id, permission_id, user_id, creation_date) VALUES (1, 2, 1, current_timestamp);
INSERT INTO sso_roles_permissions (role_id, permission_id, user_id, creation_date) VALUES (1, 3, 1, current_timestamp);
INSERT INTO sso_roles_permissions (role_id, permission_id, user_id, creation_date) VALUES (1, 4, 1, current_timestamp);
INSERT INTO sso_roles_permissions (role_id, permission_id, user_id, creation_date) VALUES (1, 5, 1, current_timestamp);
INSERT INTO sso_roles_permissions (role_id, permission_id, user_id, creation_date) VALUES (1, 6, 1, current_timestamp);
INSERT INTO sso_roles_permissions (role_id, permission_id, user_id, creation_date) VALUES (1, 7, 1, current_timestamp);
INSERT INTO sso_roles_permissions (role_id, permission_id, user_id, creation_date) VALUES (1, 8, 1, current_timestamp);

SELECT setval('permission_id_seq', 8);
SELECT setval('sso_role_id_seq', 1);
--SELECT setval('sso_roles_permissions_id_seq', 8);

UPDATE users SET role_id = 1 WHERE id = 1;