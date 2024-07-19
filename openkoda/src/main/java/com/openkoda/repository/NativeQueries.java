package com.openkoda.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.query.sql.internal.NativeQueryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;

import static com.openkoda.controller.common.URLConstants.LOWERCASE_NUMERIC_UNDERSCORE_REGEXP;

@Repository
public class NativeQueries {
    @Autowired
    EntityManager entityManager;

    @Autowired
    private DataSource dataSource;

    public boolean createTable(String tableName){
        if(tableName.matches(LOWERCASE_NUMERIC_UNDERSCORE_REGEXP)) {
            entityManager.createNativeQuery(createTableSql(tableName)).executeUpdate();
            return true;
        }
        return false;
    }

    public String createTableSql(String tableName) {
        return "CREATE TABLE " + tableName + """
                (id bigint NOT NULL,
                 created_by character varying(255),
                 created_by_id bigint,
                 created_on timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
                 index_string character varying(16300)  DEFAULT '',
                 modified_by character varying(255),
                 modified_by_id bigint, organization_id bigint,
                 updated_on timestamp with time zone DEFAULT CURRENT_TIMESTAMP)""";
    }

    public void runUpdateQuery(String sqlUpdateScript) throws SQLException {
        Connection connection = dataSource.getConnection();
        ScriptUtils.executeSqlScript(connection, new ByteArrayResource(sqlUpdateScript.getBytes()));
    }
    public boolean ifTableExists(String tableName){
           return (Boolean) entityManager.createNativeQuery(tableExistsSql()).setParameter("tableName", tableName)
                   .getSingleResult();
    }

    public String tableExistsSql() {
        return """
                SELECT EXISTS (
                    SELECT FROM
                        pg_tables
                    WHERE
                        tablename  = :tableName)""";
    }

    @Transactional(readOnly = true)
    public List<LinkedHashMap<String, Object>> runReadOnly(String query) {
        Query q1 = entityManager.createNativeQuery(StringUtils.substringBefore(query,";"));
        NativeQueryImpl nativeQuery = (NativeQueryImpl) q1;
        nativeQuery.setResultTransformer(AliasToEntityHashMapResultTransformer.INSTANCE);
        return nativeQuery.getResultList();
    }
}
