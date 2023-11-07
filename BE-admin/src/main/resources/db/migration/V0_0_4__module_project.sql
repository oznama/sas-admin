CREATE TABLE clients (
    id bigserial not null,
    name varchar(150) not null,
    active boolean default true,
    eliminate boolean default false,
    user_id int8,
    creation_date timestamp not null,
    primary key(id)
);

CREATE TABLE employees (
    id bigserial not null,
    name varchar(50) not null,
    second_name varchar(50),
    surname varchar(50) not null,
    second_surname varchar(50),
    active boolean default true,
    eliminate boolean default false,
    user_id int8,
    creation_date timestamp not null,
    client_id int8,
    primary key(id)
);

CREATE TABLE projects (
    id bigserial not null,
    p_key varchar(15) not null unique,
    description varchar(255) not null,
    status int8 not null,
    client_id int8 not null,
    project_manager_id int8 not null,
    installation_date timestamp,
    active boolean default true,
    eliminate boolean default false,
    user_id int8,
    creation_date timestamp not null,
    primary key(id)
);

CREATE TABLE project_applications (
    id bigserial not null,
    project_id int8 not null,
    application_id int8 not null,
    amount decimal(15,2) not null,
    leader_id int8 not null,
    developer_id int8 not null,
    hours int,
    design_date timestamp,
    development_date timestamp,
    end_date timestamp,
    active boolean default true,
    eliminate boolean default false,
    user_id int8,
    creation_date timestamp not null,
    primary key (id)
);

alter table employees add constraint fk_employee_client
foreign key (client_id) references clients;

alter table projects add constraint fk_project_client
foreign key (client_id) references clients;

alter table projects add constraint fk_project_pm
foreign key (project_manager_id) references employees;

alter table project_applications add constraint fk_proj_app_project
foreign key (project_id) references projects;

alter table project_applications add constraint fk_proj_app_leader
foreign key (leader_id) references users;

alter table project_applications add constraint fk_proj_app_developer
foreign key (developer_id) references users;