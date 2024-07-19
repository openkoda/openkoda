-- @version: 1.6.0.0

CREATE TABLE public.dynamic_entity (
    id int8 NOT NULL,
    created_by varchar(255) NULL,
    created_by_id int8 NULL,
    created_on timestamptz NULL DEFAULT CURRENT_TIMESTAMP,
    modified_by varchar(255) NULL,
    modified_by_id int8 NULL,
    updated_on timestamptz NULL DEFAULT CURRENT_TIMESTAMP,
    table_name varchar(255) NOT NULL,
    CONSTRAINT dynamic_entity_pkey PRIMARY KEY (id),
    CONSTRAINT uk92trgn7q4egx40p7e4upyo7ln UNIQUE (table_name)
);

ALTER TABLE public.form ADD IF NOT EXISTS filter_columns varchar(255) NULL;
ALTER TABLE public.email ADD IF NOT EXISTS sender varchar(255) NULL;

-- @version: 1.6.1.0
ALTER TABLE public.form ADD IF NOT EXISTS show_on_organization_dashboard bool NULL;


-- @version: 1.6.2.0
-- @init

create table IF NOT EXISTS public.query_report
(
    id              bigint not null
        primary key,
    created_by      varchar(255),
    created_by_id   bigint,
    created_on      timestamp with time zone default CURRENT_TIMESTAMP,
    index_string    varchar(16300)           default ''::character varying,
    modified_by     varchar(255),
    modified_by_id  bigint,
    organization_id bigint,
    updated_on      timestamp with time zone default CURRENT_TIMESTAMP,
    name            varchar(255),
    query           varchar(1000)
);

-- @version: 1.6.2.1
-- @init
update public.roles set "privileges" = "privileges" || ',(canUseAI)'  where name in ('ROLE_ADMIN')  and "privileges" not like '%canUseAI%';


-- @version: 1.6.2.2
alter table organization add column IF NOT EXISTS main_brand_color varchar(255);
alter table organization add column IF NOT EXISTS second_brand_color varchar(255);
alter table organization add column IF NOT EXISTS logo_id bigint references file(id);
alter table organization add column IF NOT EXISTS personalize_dashboard boolean;
update organization set personalize_dashboard = false;
alter table audit add column IF NOT EXISTS entity_key varchar(255);
alter table form add column IF NOT EXISTS register_as_auditable boolean ;
update form set register_as_auditable = true;

-- @version: 1.7.0.0

CREATE TABLE IF NOT EXISTS public.dynamic_privilege (
    id int8 NOT NULL,
    category varchar(255) NULL,
    privilege_group varchar(255) NULL,
    index_string varchar(16300) NULL DEFAULT ''::character varying,
    "name" varchar(255) NOT NULL,
    removable bool NULL DEFAULT true,
    updated_on timestamptz NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT dynamic_privilege_pkey PRIMARY KEY (id),
    CONSTRAINT uk_j9c9gkfh338hs9ved624aqtu2 UNIQUE (name)
);
CREATE INDEX IF NOT EXISTS idxj9c9gkfh338hs9ved624aqtu2 ON public.dynamic_privilege USING btree (name);

-- @version: 1.7.0.1
ALTER TABLE public.dynamic_privilege ADD "label" varchar(255) NULL;
UPDATE public.dynamic_privilege SET label = "name" WHERE label IS NULL;
ALTER TABLE public.dynamic_privilege ALTER COLUMN label SET NOT NULL;

-- @version: 1.7.1.1
create table public.dynamic_entity_csv_import_row
(
    id              bigint not null
        primary key,
    created_by      varchar(255),
    created_by_id   bigint,
    created_on      timestamp with time zone default CURRENT_TIMESTAMP,
    index_string    varchar(16300)           default ''::character varying,
    modified_by     varchar(255),
    modified_by_id  bigint,
    organization_id bigint,
    updated_on      timestamp with time zone default CURRENT_TIMESTAMP,
    upload_id       bigint,
    line_number     bigint,
    valid           boolean,
    entity_key       varchar(255),
    content         jsonb
);

update roles set privileges=privileges||',(canImportData)' where name in ('ROLE_ADMIN','ROLE_ORG_ADMIN');

-- @version: 1.7.1.2
update roles set privileges=privileges||',(canCreateReports),(canReadReports)' where name in ('ROLE_ADMIN','ROLE_ORG_ADMIN');
update public.roles set "privileges" = replace("privileges",'canUseAI','canUseReportingAI')  where "privileges" like '%canUseAI%';

-- following lines contains db changes not ready yet to be executed. Once ready, replace with @version
-- When adding qierues always think about existing data and how to deal with them
-- @upcoming: 1.7.x.x

