create table form
(
    id bigint not null
        constraint form_pkey
            primary key,
    created_by varchar(255),
    created_by_id bigint,
    created_on timestamp with time zone default CURRENT_TIMESTAMP,
    index_string varchar(16300) default ''::character varying,
    modified_by varchar(255),
    modified_by_id bigint,
    organization_id bigint
        constraint fkey_organization_id
            references organization,
    updated_on timestamp with time zone default CURRENT_TIMESTAMP,
    code varchar(262144),
    name varchar(255),
    read_privilege varchar(255),
    register_api_crud_controller boolean not null,
    register_html_crud_controller boolean not null,
    table_columns varchar(255),
    write_privilege varchar(255)
);

alter table form owner to postgres;

