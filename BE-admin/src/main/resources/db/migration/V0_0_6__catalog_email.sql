-- Catalogo Estatus Projecto-Aplicacion
INSERT INTO catalog (id, value, description) VALUES (1000000011, 'Catalogo de Correos CC', 'Catalogo de correos con copia');
INSERT INTO catalog (id, value, catalog_parent_id) VALUES (2001100001, 'jaime.carreno@sas-mexico.com', 1000000011);
INSERT INTO catalog (id, value, catalog_parent_id) VALUES (2001100002, 'selene.pascalis@sas-mexico.com', 1000000011);