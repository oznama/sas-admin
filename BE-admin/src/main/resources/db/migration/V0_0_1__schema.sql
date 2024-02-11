create sequence hibernate_sequence start 1 increment 1;

CREATE TABLE companies (
    id bigserial not null,
    name varchar(150) not null,
    alias varchar(50),
    rfc varchar(15),
    address varchar(255),
    interior varchar(10),
    exterior varchar(10),
    cp varchar(7),
    locality varchar(255),--colonia
    city varchar(255),
    state varchar(255),
    country varchar(255),
    cellphone varchar(20),
    phone varchar(15),
    ext varchar(5),
    email_domain varchar(50),
    type int8,
    active boolean default true,
    eliminate boolean default false,
    created_by int8 default 1,
    creation_date timestamp default current_timestamp,
    primary key(id)
);

CREATE TABLE employees (
    id bigserial not null,
    email varchar(255) not null unique,
    name varchar(50) not null,
    second_name varchar(50),
    surname varchar(50) not null,
    second_surname varchar(50),
    city varchar(255),
    country varchar(255),
    cellphone varchar(20),
    phone varchar(255),
    ext varchar(5),
    image varchar(255),
    active boolean default true,
    eliminate boolean default false,
    company_id int8,
    boss_id int8,
    position_id int8,
    created_by int8 default 1,
    creation_date timestamp default current_timestamp,
    primary key(id)
);

create table users (
    id bigserial not null,
    password varchar(255) not null,
    active boolean default true,
    eliminate boolean default false,
    employee_id int8,
    role_id int8,
    created_by int8 default 1,
    creation_date timestamp default current_timestamp,
    primary key (id)
);

create table catalog (
    id  bigserial not null,
    value varchar(255),
    description varchar(255),
    status int8 default 2000100001,
    internal boolean default false,
    catalog_parent_id int8,
    created_by int8 default 1,
    company_id int8 default 1,
    creation_date timestamp default current_timestamp,
    primary key (id)
);

create table permission (
    id  bigserial not null,
    name varchar(255),
    description varchar(255),
    visible boolean default true,
    created_by int8 default 1,
    creation_date timestamp default current_timestamp,
    primary key (id)
);


create table sso_role (
    id  bigserial not null,
    name varchar(255),
    description varchar(255),
    created_by int8 default 1,
    creation_date timestamp default current_timestamp,
    primary key (id)
);

create table sso_roles_permissions (
    id  bigserial not null,
    role_id int8,
    permission_id int8,
    active boolean default true,
    created_by int8 default 1,
    creation_date timestamp default current_timestamp,
    primary key (id)
);

CREATE TABLE applications (
    app_name varchar(50) not null,
    description varchar(255) not null,
    active boolean default true,
    eliminate boolean default false,
    created_by int8 default 1,
    creation_date timestamp default current_timestamp,
    company_id int8 not null,
    primary key(app_name)
);

CREATE TABLE projects (
    p_key varchar(15) not null,
    description varchar(255) not null,
    status int8 not null default 2000200001,
    company_id int8 not null,
    project_manager_id int8 not null,
    installation_date timestamp,
    amount decimal(15,2) not null,
    tax decimal(15,2) not null,
    total decimal(15,2) not null,
    observations text,
    qa_start_date timestamp,
    qa_end_date timestamp,
    delivery_date timestamp,
    follow_code varchar(2),
    active boolean default true,
    eliminate boolean default false,
    created_by int8 default 1,
    creation_date timestamp default current_timestamp,
    primary key(p_key)
);

CREATE TABLE project_applications (
    id bigserial not null,
    p_key varchar(15) not null,
    app_name varchar(50) not null,
    amount decimal(15,2) not null,
    tax decimal(15,2) not null,
    total decimal(15,2) not null,
    leader_id int8 not null,
    developer_id int8 not null,
    hours int,
    start_date timestamp,
    design_date timestamp,
    development_date timestamp,
    end_date timestamp,
    observations text,
    active boolean default true,
    eliminate boolean default false,
    created_by int8 default 1,
    creation_date timestamp default current_timestamp,
    primary key (id)
);

CREATE TABLE orders (
    order_num varchar(15) not null,
    order_date timestamp,
    status int8,
    amount decimal(15,2) not null,
    tax decimal(15,2) not null,
    total decimal(15,2) not null,
    requisition varchar(15) unique,
    requisition_date timestamp,
    requisition_status int8,
    observations text,
    active boolean default true,
    eliminate boolean default false,
    created_by int8 default 1,
    creation_date timestamp default current_timestamp,
    p_key varchar(15) not null,
    primary key(order_num)
);

CREATE TABLE invoices (
    invoice_num varchar(15) not null,
    issued_date timestamp not null,
    payment_date timestamp,
    percentage int4 not null,
    status int8,
    amount decimal(15,2) not null,
    tax decimal(15,2) not null,
    total decimal(15,2) not null,
    observations text,
    active boolean default true,
    eliminate boolean default false,
    created_by int8 default 1,
    creation_date timestamp default current_timestamp,
    order_num varchar(15) not null,
    primary key(invoice_num)
);

CREATE TABLE log_movement (
    id bigserial not null,
    table_name varchar(255) not null,
    record_id int8 not null,
    created_by int8 not null,
    user_fullname varchar(255) not null,
    creation_date timestamp default current_timestamp,
    event_id int8 not null,
    description varchar,
    primary key (id)
);

alter table catalog add constraint fk_catalog_parent foreign key (catalog_parent_id) references catalog;

alter table catalog add constraint fk_catalog_company foreign key (company_id) references companies;

alter table permission add constraint unique_perm_name unique (name);

alter table sso_roles_permissions add constraint fk_sso_permission foreign key (permission_id) references permission;

alter table sso_roles_permissions add constraint fk_sso_role foreign key (role_id) references sso_role;

alter table users add constraint fk_user_role foreign key (role_id) references sso_role;

alter table users add constraint fk_users_employee foreign key (employee_id) references employees;

alter table log_movement add constraint log_movement_user foreign key (created_by) references users;

alter table employees add constraint fk_employee_company foreign key (company_id) references companies;

alter table employees add constraint fk_employee_boss foreign key (boss_id) references employees;

alter table projects add constraint fk_project_company foreign key (company_id) references companies;

alter table projects add constraint fk_project_pm foreign key (project_manager_id) references employees;

alter table project_applications add constraint fk_proj_app_project foreign key (p_key) references projects;

alter table project_applications add constraint fk_proj_app_application foreign key (app_name) references applications;

alter table project_applications add constraint fk_proj_app_leader foreign key (leader_id) references employees;

alter table project_applications add constraint fk_proj_app_developer foreign key (developer_id) references employees;

alter table orders add constraint fk_order_project foreign key (p_key) references projects;

alter table invoices add constraint fk_invoice_order foreign key (order_num) references orders;