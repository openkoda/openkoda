package com.openkoda.repository;

import com.openkoda.core.repository.common.UnsecuredFunctionalRepositoryWithLongId;
import com.openkoda.model.DynamicEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public interface DynamicEntityRepository extends UnsecuredFunctionalRepositoryWithLongId<DynamicEntity>{

    @Query(value="select de.tableName from DynamicEntity de")
    List<Object> getTableNames();
    @Query(nativeQuery = true, value = """
            select table_name, string_agg(' ' || column_name, ',') from
               (SELECT table_name, column_name FROM information_schema.columns WHERE table_schema = 'public' AND table_name IN
               (select distinct tablename from pg_tables
                inner join dynamic_entity as de on de.table_name=pg_tables.tablename and pg_tables.schemaname='public')) x
            group by table_name
            """)
    List<Object[]> getDynamicTablesColumnNamesQuery();

    default Map<String,String> getDynamicTablesColumnNames(){
        return getDynamicTablesColumnNamesQuery().stream().collect(Collectors.toMap(l -> (String) ((Object[])l)[0], l ->  (String)((Object[])l)[1]));
    }
}
