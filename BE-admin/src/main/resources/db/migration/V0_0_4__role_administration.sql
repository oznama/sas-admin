ALTER TABLE sso_role
ADD COLUMN active BOOLEAN DEFAULT TRUE,
ADD COLUMN eliminate BOOLEAN DEFAULT FALSE;

ALTER TABLE sso_roles_permissions
ADD COLUMN eliminate BOOLEAN DEFAULT FALSE;