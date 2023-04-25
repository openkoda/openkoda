/*
MIT License

Copyright (c) 2016-2022, Codedose CDX Sp. z o.o. Sp. K. <stratoflow.com>

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
documentation files (the "Software"), to deal in the Software without restriction, including without limitation
the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice
shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR
A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.openkoda.core.multitenancy;

import com.openkoda.core.tracker.LoggingComponentWithRequestId;
import jakarta.persistence.*;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Helper component for executing Queries
 */
@Component
public class QueryExecutor implements LoggingComponentWithRequestId {

    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    /**
     * Reads resource from classpath
     */
    String readResource(String path) {
        try (InputStream resourceIO = this.getClass().getResourceAsStream(path)) {
            return IOUtils.toString(resourceIO);
        } catch (Exception e) {
            error(e, "[readResource] Can't find or read resource {}", path);
        }
        return null;
    }

    /**
     * Return list of tenant schema names.
     * Tenant (ie. organization related) schemas start with prefix 'org_'
     */
    protected List<String> getSchemas(EntityManager em) {
        return (List<String>) em.createNativeQuery("SELECT schema_name FROM information_schema.schemata where schema_name like 'org_%';").getResultList();
    }

    /**
     * Executes (in transaction) SQL script from classpath resource
     * @param classpathResource
     */
    public void runQueryFromResourceInTransaction(String classpathResource) {
        String queryString = readResource(classpathResource);
        runQueries(true, false, queryString);
    }

    /**
     * Executes (without transaction) SQL script from classpath resource
     * @param classpathResource
     */
    public void runQueryFromResource(String classpathResource) {
        String queryString = readResource(classpathResource);
        runQueries(false, false, queryString);
    }

    /**
     * Executes (in transaction) array of SQL queries
     * @param queries Array of SQL queries
     */
    public boolean runQueriesInTransaction(String ... queries) {
        return runQueries(true, false, queries);
    }

    /**
     * Executes (without transaction) array of SQL queries
     * @param queries Array of SQL queries
     */
    public boolean runQueries(String ... queries) {
        return runQueries(false, false, queries);
    }

    /**
     * Run SQL queries
     * @param transactional whether it should be executed in transaction
     * @param logTime whether execution time should be logged in debug
     * @param queries array of SQL queries to execute
     * @return
     */
    private boolean runQueries(boolean transactional, boolean logTime, String ... queries) {
        long start = System.nanoTime();
        runEntityManagerOperation(transactional, em -> {
            executeQueries(em, queries);
            return null;
        });
        long stop = System.nanoTime();
        debug("[runQueries] Successfully in {} us", (stop - start) / 1000);
        return true;

    }

    public <T> T runEntityManagerOperation(Function<EntityManager, T> query) {
        return runEntityManagerOperationForOrg(null, false, (em, orgId) -> query.apply(em));
    }

    public <T> T runEntityManagerOperation(boolean transactional, Function<EntityManager, T> query) {
        return runEntityManagerOperationForOrg(null, transactional, (em, orgId) -> query.apply(em));
    }

    public <T> T runEntityManagerOperationInTransaction(Function<EntityManager, T> query) {
        return runEntityManagerOperationForOrg(null, true, (em, orgId) -> query.apply(em));
    }

    /**
     * Executes {@link EntityManager} operation for provided
     * @param orgId organization id for which the operation should be executed
     * @param transactional whether it should be executed in transaction
     * @param operation function (EntityManager, organizationId) -> result
     * @param <T> type of the operation result
     * @return
     */
    <T> T runEntityManagerOperationForOrg(Long orgId, boolean transactional, BiFunction<EntityManager, Long, T> operation) {
        debug("[runEntityManagerOperationForOrg] org {} t {}", orgId, transactional);
        EntityManager em = null;
        T result = null;
        EntityTransaction transaction = null;
        try {
            em = entityManagerFactory.createEntityManager();
            em.setFlushMode(FlushModeType.AUTO);
            if (transactional) {
                transaction = em.getTransaction();
                transaction.begin();
            }

            Object searchPath = em.createNativeQuery("show search_path").getSingleResult();
            TenantResolver.TenantedResource tr = TenantResolver.getTenantedResource();
            debug("[runEntityManagerOperationForOrg] search path {} tr {}", searchPath, tr);
            result = operation.apply(em, orgId);

            if (transactional) {
                transaction.commit();
            }
            em.close();
        } catch (Exception e) {
            error("[runEntityManagerOperationForOrg]", e);
            if (transaction != null) {
                transaction.rollback();
            }
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return result;
    }

    /**
     * Execute native SQL queries using entity manager
     */
    void executeQueries(EntityManager em, String... queries) {
        for (String queryString : queries) {
            debug("[executeQueries] {}", queryString);
            int result = em.createNativeQuery(queryString).executeUpdate();
            debug("[executeQueries] {}", result);
        }
    }

}
