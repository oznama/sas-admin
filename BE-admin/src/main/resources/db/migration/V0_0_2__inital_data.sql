-- Usuarios
INSERT INTO users (id, name, surname, email, phone, creation_date, active) VALUES (1, 'admin', 'admin', 'admin@sas-mexico.com', '555522558866', current_timestamp, 'true');
INSERT INTO user_security (user_id, password) VALUES (1, 'Ea6SCcSRF9rJUxhtMk3bXg==');
SELECT setval('users_id_seq', 1);

-- Catalogos

---- Maquina de estados
INSERT INTO catalog (id, value, description, type, user_id, creation_date) VALUES (1000000001, 'Catalogo de estatus', 'Maquina de estado', 2000200002, 1, current_timestamp);
INSERT INTO catalog (id, value, description, type, user_id, creation_date, catalog_parent_id) VALUES (2000100001, 'Activo', 'Registro activo', 2000200002, 1, current_timestamp, 1000000001);
INSERT INTO catalog (id, value, description, type, user_id, creation_date, catalog_parent_id) VALUES (2000100002, 'Inactivo', 'Registro inactivo', 2000200002, 1, current_timestamp, 1000000001);
INSERT INTO catalog (id, value, description, type, user_id, creation_date, catalog_parent_id) VALUES (2000100003, 'Eliminado', 'Registro eliminado', 2000200002, 1, current_timestamp, 1000000001);

---- Catalogo de tipos de catalogo
INSERT INTO catalog (id, value, description, type, user_id, creation_date) VALUES (1000000002, 'Tipo Catalogo', 'Tipo de catalogo interno o externo', 2000200002, 1, current_timestamp);
INSERT INTO catalog (id, value, description, type, user_id, creation_date, catalog_parent_id) VALUES (2000200001, 'Interno', 'Tipo de catalogo interno', 2001200002, 1, current_timestamp, 1000000002);
INSERT INTO catalog (id, value, description, type, user_id, creation_date, catalog_parent_id) VALUES (2000200002, 'Externo', 'Tipo de catalogo externo', 2001200002, 1, current_timestamp, 1000000002);

---- Crear catalogo bitacora (eventos, detalle)
INSERT INTO catalog (id, value, description, user_id, creation_date) VALUES (1000000003, 'Bitacora evento', 'Catalogo de eventos de bitacora', 1, current_timestamp);
INSERT INTO catalog (id, value, description, user_id, creation_date, catalog_parent_id) VALUES (2000300001, 'Insert', 'Insert', 1, current_timestamp, 1000000003);
INSERT INTO catalog (id, value, description, user_id, creation_date, catalog_parent_id) VALUES (2000300002, 'Update', 'Update', 1, current_timestamp, 1000000003);
INSERT INTO catalog (id, value, description, user_id, creation_date, catalog_parent_id) VALUES (2000300003, 'Delete', 'Delete', 1, current_timestamp, 1000000003);

INSERT INTO catalog (id, value, description, user_id, creation_date) VALUES (1000000004, 'Bitacora detalle', 'Catalogo de detalle de bitacora', 1, current_timestamp);
INSERT INTO catalog (id, value, description, user_id, creation_date, catalog_parent_id) VALUES (2000400001, 'Insert', 'New record', 1, current_timestamp, 1000000004);
INSERT INTO catalog (id, value, description, user_id, creation_date, catalog_parent_id) VALUES (2000400002, 'Update', 'Change data', 1, current_timestamp, 1000000004);
INSERT INTO catalog (id, value, description, user_id, creation_date, catalog_parent_id) VALUES (2000400003, 'Delete', 'Physical delete', 1, current_timestamp, 1000000004);
INSERT INTO catalog (id, value, description, user_id, creation_date, catalog_parent_id) VALUES (2000400004, 'Status', 'Status change', 1, current_timestamp, 1000000004);
INSERT INTO catalog (id, value, description, user_id, creation_date, catalog_parent_id) VALUES (2000400005, 'Eliminate', 'Logic delete', 1, current_timestamp, 1000000004);

-- Roles
INSERT INTO sso_role (id, name, description, user_id, creation_date) VALUES (1, 'ROOT', 'Usuario root', 1, current_timestamp);
INSERT INTO sso_role (id, name, description, user_id, creation_date) VALUES (2, 'ADMIN', 'Usuario Administrador', 1, current_timestamp);
INSERT INTO sso_role (id, name, description, user_id, creation_date) VALUES (3, 'DEV', 'Desarrollador', 1, current_timestamp);

SELECT setval('sso_role_id_seq', 3);

UPDATE users SET role_id = 1 WHERE id = 1;