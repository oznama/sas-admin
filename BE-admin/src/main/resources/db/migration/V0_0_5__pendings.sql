-- Catalogo Estatus Projecto-Aplicacion
INSERT INTO catalog (id, value, description) VALUES (1000000010, 'Estatus Projecto-Aplicacion', 'Estatus para projectos y aplicaciones');
INSERT INTO catalog (id, value, catalog_parent_id) VALUES (2001000001, 'Pendiente', 1000000010);
INSERT INTO catalog (id, value, catalog_parent_id) VALUES (2001000002, 'En proceso', 1000000010);
INSERT INTO catalog (id, value, catalog_parent_id) VALUES (2001000003, 'Completado', 1000000010);

ALTER TABLE project_applications
ADD COLUMN design_status int8,
ADD COLUMN development_status int8,
ADD COLUMN end_status int8;