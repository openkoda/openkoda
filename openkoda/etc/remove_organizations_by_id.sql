create or replace procedure remove_organizations_by_id(in org_id bigint[]) language plpgsql as $$

declare

    -- example usage
    -- call remove_organizations_by_id(array [196, 198]);

    query text;
    tables record;
    orgs_str text;
    skip text := ' users fb_users ldap_users linkedin_users google_users salesforce_users token audit ';

begin

    -- Whenever we try to delete a row from table
    -- Postgres needs to be sure it is not currently being referenced by any foreign key
    -- This check is pretty fast on indexed columns, but terribly slow on ones that were not indexed
    -- During implementation of this script I noticed high speed-up after creating indexes on

    select array_to_string(org_id,',') into orgs_str;
    if length(orgs_str) > 0 then
        for tables in select t.table_name from information_schema.tables t
        inner join information_schema.columns c
            on c.table_name = t.table_name
            and c.table_schema = t.table_schema
        where c.column_name = 'organization_id'
            and t.table_schema not in ('information_schema', 'pg_catalog')
            and t.table_type = 'BASE TABLE'
        group by t.table_name
        loop
            continue when position(' '|| tables.table_name || ' ' in skip) > 0;
            query := 'delete from '|| tables.table_name ||' where organization_id in ('|| orgs_str ||');';
            execute query;
        end loop;
    end if;
    delete from organization where id = any (org_id);
end;
$$;