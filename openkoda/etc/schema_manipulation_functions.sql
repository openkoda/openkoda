--
-- allows to modify model in all schemas at once, eg:
-- select fn_ddl_for_each_schema('alter table item add column new_column varchar(4)')
-- select fn_ddl_for_each_schema('update item set new_column = ''test''');
--
CREATE OR REPLACE FUNCTION fn_ddl_for_each_schema(query varchar) RETURNS void AS $$
DECLARE
    sn RECORD;
	ir integer;
BEGIN
    SET search_path TO public;

    FOR sn IN SELECT schema_name FROM information_schema.schemata where schema_name like 'org_%' LOOP

        PERFORM set_config('search_path', sn.schema_name || ',public', false);
        RAISE NOTICE 'Executing DDL for schema %', sn.schema_name;
        EXECUTE query;
        --RAISE NOTICE 'DDL result for schema %', ir;

    END LOOP;

    SET search_path TO public;

    RAISE NOTICE 'Executed queries.';
END;
$$ LANGUAGE plpgsql;



--
-- allows to query all schemas at once, eg:
-- select fn_sql_for_each_schema('select id, new_column from item');
--
CREATE OR REPLACE FUNCTION fn_sql_for_each_schema(query varchar) RETURNS void AS $$
DECLARE
    sn RECORD;
	ir RECORD;
BEGIN
    SET search_path TO public;

    FOR sn IN SELECT schema_name FROM information_schema.schemata where schema_name like 'org_%' LOOP

        PERFORM set_config('search_path', sn.schema_name || ',public', false);
        RAISE NOTICE 'Executing query for schema %', sn.schema_name;
        FOR ir IN EXECUTE query
		LOOP
			RAISE NOTICE '%: %', sn.schema_name, ir;
		END LOOP;

    END LOOP;

    SET search_path TO public;

    RAISE NOTICE 'Executed queries.';
END;
$$ LANGUAGE plpgsql;
