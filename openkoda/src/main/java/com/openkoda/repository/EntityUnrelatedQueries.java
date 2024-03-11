package com.openkoda.repository;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.repository.query.Param;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.openkoda.controller.common.URLConstants.LOWERCASE_NUMERIC_UNDERSCORE_REGEXP;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Repository
public class EntityUnrelatedQueries {
    @Autowired
    EntityManager entityManager;

    @Autowired
    private DataSource dataSource;

    @Transactional(propagation = REQUIRES_NEW)
    public boolean createTableIfNotExists(String tableName){
        if(tableName.matches(LOWERCASE_NUMERIC_UNDERSCORE_REGEXP)) {
            entityManager.createNativeQuery("CREATE TABLE IF NOT EXISTS " + tableName + """
                    (id bigint NOT NULL,
                     created_by character varying(255),
                     created_by_id bigint,
                     created_on timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
                     index_string character varying(16300)  DEFAULT '',
                     modified_by character varying(255),
                     modified_by_id bigint, organization_id bigint,
                     updated_on timestamp with time zone DEFAULT CURRENT_TIMESTAMP)"""
                    ).executeUpdate();
            return true;
        }
        return false;
    }
    @SuppressWarnings("unchecked")
    public List<Object> getTableNamesWithPattern(@Param("tableNamePattern") String tableNamePattern){
        return entityManager.createNativeQuery("select tablename from pg_tables where schemaname = 'public' and tablename ilike :tableNamePattern")
                .setParameter("tableNamePattern", tableNamePattern)
                .getResultList();
    }
    @SuppressWarnings("unchecked")
    public Map<String,String> getColumnNames(@Param("tableNamePattern") String tableNamePattern){
        return (Map<String,String>) entityManager.createNativeQuery("""
                        select table_name, string_agg(' ' || column_name, ',') from
                        (SELECT table_name, column_name FROM information_schema.columns WHERE table_schema = 'public' AND table_name IN
                        (select tablename from pg_tables where schemaname = 'public' and tablename ilike :tableNamePattern)) x
                        group by table_name""")
                .setParameter("tableNamePattern", tableNamePattern)
                .getResultList().stream().collect(Collectors.toMap(l -> (String) ((Object[])l)[0], l ->  (String)((Object[])l)[1]));
    }
    @SuppressWarnings("unchecked")
    public String getColumnNamesForTable(@Param("tableName") String tableName){
        return  (String) entityManager.createNativeQuery("SELECT string_agg(' ' || column_name,',') FROM information_schema.columns WHERE table_schema = 'public' AND table_name = :tableName")
                .setParameter("tableName", tableName)
                .getSingleResult();
    }

    @Transactional(propagation = REQUIRES_NEW)
    public void runUpdateQuery(String sqlUpdateScript) throws SQLException {
        Connection connection = dataSource.getConnection();
        ScriptUtils.executeSqlScript(connection, new ByteArrayResource(sqlUpdateScript.getBytes()));
    }
    public boolean ifTableExists(String tableName){
           return (Boolean) entityManager.createNativeQuery("""
                    SELECT EXISTS (
                        SELECT FROM
                            pg_tables
                        WHERE
                            schemaname = 'public' AND
                            tablename  = :tableName)""").setParameter("tableName", tableName)
                   .getSingleResult();
    }
}
