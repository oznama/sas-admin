create sequence hibernate_sequence start 1 increment 1;

create table users (
    id  bigserial not null,
    email varchar(255) not null,
    name varchar(255) not null,
    surname varchar(255) not null,
    second_surname varchar(255),
    phone varchar(255) not null,
    image varchar(255),
    active boolean default true,
    eliminate boolean default false,
    user_id int8,
    creation_date timestamp not null,
    role_id int8,
    primary key (id)
);

create table catalog (
    id  bigserial not null,
    value varchar(255),
    description varchar(255),
    is_required boolean default false,
    status int8 default 2000100001,
    type int8 default 2000200001,
    user_id int8 not null,
    creation_date timestamp not null,
    catalog_parent_id int8,
    primary key (id)
);

create table catalog_modules (
    id  bigserial not null,
    catalog_module_id int8,
    catalog_id int8,
    user_id int8 not null,
    creation_date timestamp not null,
    primary key (id)
);

create table permission (
    id  bigserial not null,
    name varchar(255),
    description varchar(255),
    paths_allowed varchar(255),
    visible boolean default true,
    user_id int8 not null,
    creation_date timestamp not null,
    primary key (id)
);


create table sso_role (
    id  bigserial not null,
    name varchar(255),
    description varchar(255),
    user_id int8 not null,
    creation_date timestamp not null,
    primary key (id)
);

create table sso_roles_permissions (
    id  bigserial not null,
    role_id int8,
    permission_id int8,
    active boolean default true,
    user_id int8 not null,
    creation_date timestamp not null,
    primary key (id)
);

create table user_confirmation_token (
    id int8 not null,
    confirmation_token varchar(255),
    used boolean,
    expired boolean,
    user_id int8 not null,
    creation_date timestamp not null,
    primary key (id)
);

create table user_security (
    email_verified boolean default false,
    password varchar(255),
    last_session timestamp,
    user_id int8 not null,
    primary key (user_id)
);

CREATE TABLE log_movement (
    id bigserial not null,
    table_name varchar(255) not null,
    record_id varchar(255),
    user_id int8 not null,
    creation_date timestamp not null,
    event_id int8 not null,
    detail_id int8 not null,
    primary key (id)
);

alter table catalog add constraint fk_catalog_parent
foreign key (catalog_parent_id) references catalog;

alter table catalog add constraint fk_catalog_user
foreign key (user_id) references users;

alter table catalog_modules add constraint fk_catalog_module1
foreign key (catalog_id) references catalog;

alter table catalog_modules add constraint fk_catalog_module2
foreign key (catalog_module_id) references catalog;

alter table permission add constraint unique_perm_name unique (name);
alter table permission add constraint permission_user foreign key (user_id) references users;

alter table sso_role add constraint sso_role_user foreign key (user_id) references users;

alter table sso_roles_permissions add constraint fk_sso_permission
foreign key (permission_id) references permission;

alter table sso_roles_permissions add constraint fk_sso_role
foreign key (role_id) references sso_role;

alter table sso_roles_permissions add constraint sso_role_permissions_user
foreign key (user_id) references users;

alter table user_confirmation_token add constraint fk_confirm_user
foreign key (user_id) references users;

alter table users add constraint fk_user_role
foreign key (role_id) references sso_role;

alter table users add constraint users_user foreign key (user_id) references users;

alter table user_security add constraint fk_sec_user
foreign key (user_id) references users;

alter table user_security add constraint user_security_user foreign key (user_id) references users;

alter table log_movement add constraint log_movement_user foreign key (user_id) references users;