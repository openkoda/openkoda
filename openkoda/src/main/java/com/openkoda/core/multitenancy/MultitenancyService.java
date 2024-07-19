/*
MIT License

Copyright (c) 2016-2023, Openkoda CDX Sp. z o.o. Sp. K. <openkoda.com>

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

import com.openkoda.core.helper.ReadableCode;
import com.openkoda.core.tracker.LoggingComponentWithRequestId;
import com.openkoda.repository.organization.OrganizationRepository;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A helper for multitenancy support.
 * @author Arkadiusz Drysch (adrysch)
 */
@Component
public class MultitenancyService implements LoggingComponentWithRequestId, ReadableCode {

    private static final int MAX_THREADS = 16;

    @Value("${is.multitenancy:false}")
    private boolean isMultitenancy;
    private static boolean isMultitenancyStatic;

    @Inject
    OrganizationRepository organizationRepository;
    List<String> tenantedTables = Collections.emptyList();
    Set<String> dynamicTenantedTables = new HashSet<>();
    List<String> tenantInitializationScripts = Collections.emptyList();
    QueryExecutor queryExecutor;
    @Inject
    TenantResolver tenantResolver;

    public MultitenancyService(
            QueryExecutor queryExecutor,
            @Value("${tenant.initialization.table.names.commaseparated:}") String tables,
            @Value("${tenant.initialization.scripts.commaseparated:}") String scripts
    ) {
        this.queryExecutor = queryExecutor;
        if (StringUtils.isNotBlank(scripts)) {
            tenantInitializationScripts = Arrays.stream(scripts.split(",")).map(a -> StringUtils.trim(a)).collect(Collectors.toList());
        }
        if (StringUtils.isNotBlank(tables)) {
            tenantedTables = Arrays.stream(tables.split(",")).map(a -> StringUtils.trim(a)).collect(Collectors.toList());
        }
    }

    /**
     * Initialization of the service.
     */
    @PostConstruct
    void init() {
        isMultitenancyStatic = isMultitenancy;
    }

    public static boolean isMultitenancy() {
        return isMultitenancyStatic;
    }

    /**
     * Collects result from futures. Executes {@link Future#get()} on each provided future
     * @return a list of results
     */
    protected <T> List<T> collect(List<Future<T>> r) {
        if (r == null) {
            return Collections.emptyList();
        }
        List<T> result = new ArrayList<>(r.size());
        try {
            for (Future<T> f : r) {
                result.add(f.get());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public void addTenantedTables(List<String> tableNames) {
        this.dynamicTenantedTables.addAll(tableNames);
    }
    public void removeTenantedTables(List<String> tableNames) {
        this.dynamicTenantedTables.removeAll(tableNames);
    }

    public <T> List<Future<T>> runForAllTenants(long timeoutInMilliseconds, Function<Long, T> operation)  {
            return runForAllTenants(timeoutInMilliseconds, false, false, operation, null, null);
    }

    /**
     * Run function for all organisations synchronously
     * @param timeoutInMilliseconds total timeout for the operation
     * @param operation Function Organization ID -> result
     * @param <T> result type
     */
    public <T> List<T> runForAllTenantsAndWait(long timeoutInMilliseconds, Function<Long, T> operation)  {
            return collect(runForAllTenants(timeoutInMilliseconds, false, true, operation, null, null));
    }

    /**
     * Run {@link QueryExecutor} function for all organisations asynchronously
     * @param timeoutInMilliseconds total timeout for the operation
     * @param operation {@link BiFunction} ({@link QueryExecutor}, Organization ID) -> list of {@link Future} results
     * @param <T> result type
     */
    public <T> List<Future<T>> runQueryExecutorForAllTenants(long timeoutInMilliseconds, BiFunction<QueryExecutor, Long, T> operation)  {
            return runForAllTenants(timeoutInMilliseconds, false, false, null, operation, null);
    }

    /**
     * Run {@link QueryExecutor} function for all organisations synchronously
     * @param timeoutInMilliseconds total timeout for the operation
     * @param operation {@link BiFunction} ({@link QueryExecutor}, Organization ID) -> list of results
     * @param <T> result type
     */
    public <T> List<T> runQueryExecutorForAllTenantsAndWait(long timeoutInMilliseconds, BiFunction<QueryExecutor, Long, T> operation)  {
        return collect(runForAllTenants(timeoutInMilliseconds, false, true, null, operation, null));
    }

    /**
     * Run {@link EntityManager} function for all organisations asynchronously
     * @param timeoutInMilliseconds total timeout for the operation
     * @param operation {@link BiFunction} ({@link EntityManager}, Organization ID) -> list of {@link Future} results
     * @param <T> result type
     */
    public <T> List<Future<T>> runEntityManagerForAllTenants(long timeoutInMilliseconds, BiFunction<EntityManager, Long, T> operation)  {
        return runForAllTenants(timeoutInMilliseconds, false, false, null, null, operation);
    }

    /**
     * Run {@link EntityManager} function for all organisations synchronously
     * @param timeoutInMilliseconds total timeout for the operation
     * @param operation {@link BiFunction} ({@link EntityManager}, Organization ID) -> list of results
     * @param <T> result type
     */
    public <T> List<T> runEntityManagerForAllTenantsAndWait(long timeoutInMilliseconds, BiFunction<EntityManager, Long, T> operation)  {
            return collect(runForAllTenants(timeoutInMilliseconds, false, true, null, null, operation));
    }

    /**
     * Run {@link EntityManager} function for all organisations asynchronously  in transaction
     * @param timeoutInMilliseconds total timeout for the operation
     * @param operation {@link BiFunction} ({@link EntityManager}, Organization ID) -> list of {@link Future} results
     * @param <T> result type
     */
    public <T> List<Future<T>> runEntityManagerForAllTenantsInTransaction(long timeoutInMilliseconds, BiFunction<EntityManager, Long, T> operation)  {
        return runForAllTenants(timeoutInMilliseconds, true, false, null, null, operation);
    }

    /**
     * Run {@link EntityManager} function for all organisations synchronously in transaction
     * @param timeoutInMilliseconds total timeout for the operation
     * @param operation {@link BiFunction} ({@link EntityManager}, Organization ID) -> list of results
     * @param <T> result type
     */
    public <T> List<T> runEntityManagerForAllTenantsAndWaitInTransaction(long timeoutInMilliseconds, BiFunction<EntityManager, Long, T> operation)  {
            return collect(runForAllTenants(timeoutInMilliseconds, true, true, null, null, operation));
    }

    /**
     * Run function for provided organizations synchronously
     * @param timeoutInMilliseconds timeout for total execution
     * @param orgIds list of organizations to run the function on
     * @param f function to execute
     * @param <T> result type
     */
    public <T> List<T> runForTenantsAndWait(long timeoutInMilliseconds, Collection<Long> orgIds, Function<Long, T> f) {
        return collect(runForTenants(timeoutInMilliseconds, false, true, orgIds.stream(), f, null, null));
    }

    /**
     * Run function for provided organization synchronously
     * @param timeoutInMilliseconds timeout for total execution
     * @param orgId id of organization to run the function on
     * @param f function to execute
     * @param <T> result type
     */
    public <T> List<T> runForTenantAndWait(long timeoutInMilliseconds, Long orgId, Function<Long, T> f) {
        return collect(runForTenants(timeoutInMilliseconds, false, true, Stream.of(orgId), f, null, null));
    }

    /**
     * Run {@link QueryExecutor} function for provided organization synchronously
     * @param timeoutInMilliseconds timeout for total execution
     * @param orgId id of organization to run the function on
     * @param operation function to execute
     * @param <T> result type
     */
    public <T> List<T> runQueryExecutorForTenantAndWait(long timeoutInMilliseconds, Long orgId, BiFunction<QueryExecutor, Long, T> operation)  {
        return collect(runForTenants(timeoutInMilliseconds, false, true, Stream.of(orgId), null, operation, null));
    }


    /**
     * Create tenant setup for newly created organization being aware of multitenancy configuration.
     * If the application instance is configured for single schema multitenancy the function will do noop.
     * @param organizationId newly created organization id that should be configured for multitenency.
     */
    public boolean createTenant(long organizationId) {
        if (not(isMultitenancy)) {
            return false;
        }

        TenantResolver.setTenantedResource(new TenantResolver.TenantedResource(organizationId));
        debug("[createTenant] org {} set tenanted resource to {}", organizationId, TenantResolver.getTenantedResource());

        String schemaName = "org_" + organizationId;
        queryExecutor.runQueriesInTransaction(String.format("create schema %s", schemaName));
        debug("[createTenant] org {} created schema {}", organizationId, schemaName);

        String[] tenantTablesDDLs = Stream.concat(tenantedTables.stream(), dynamicTenantedTables.stream())
                .map(a -> String.format("create table %s.%s (like public.%s including all excluding constraints excluding indexes)", schemaName, a, a)).toArray(String[]::new);

        for(String tt : tenantTablesDDLs) {
            debug("[createTenant] org {} running {}", organizationId, tt);
            queryExecutor.runQueriesInTransaction(tt);
        }

        for(String s : tenantInitializationScripts) {
            String queryString = queryExecutor.readResource(s);
            debug("[createTenant] org {} running {}", organizationId, s);
            queryExecutor.runQueriesInTransaction(queryString);
        }

        debug("[createTenant] org {} tenant creation completed", organizationId);
        return true;
    }

    /**
     * Renames tenant schema from org_[id] to deleted_[id].
     * If the application instance is configured for single schema multitenancy the function will do noop.
     * Used on organization delete.
     * @param organizationId organization id
     * @param assignedDatasource datasource assigned to that organization id
     * @return schema name after renaming
     */
    public String markSchemaAsDeleted(long organizationId, int assignedDatasource) {
        if (not(isMultitenancy)) {
            return null;
        }
        TenantResolver.setTenantedResource(new TenantResolver.TenantedResource(assignedDatasource));
        debug("[markSchemeAsDeleted] org {} set tenanted resource to {}", organizationId, TenantResolver.getTenantedResource());

        String schemaName = "org_" + organizationId;
        String newSchemaName = "deleted_" + organizationId;

        queryExecutor.runQueriesInTransaction(String.format("call rename_schema('%s', '%s');", schemaName, newSchemaName));
        debug("[markSchemeAsDeleted] org {} renamed schema {} to {}", organizationId, schemaName, newSchemaName);


        return newSchemaName;
    }

    /**
     * Drops constraints from schema in order to organization for removal.
     * If the application instance is configured for single schema multitenancy the function will do noop.
     * @param organizationId organization id
     * @param schemaName name of the schema
     * @param assignedDatasource datasource assigned to the organization
     */
    public boolean dropSchemaConstraints(long organizationId, String schemaName, int assignedDatasource) {
        if (not(isMultitenancy)) {
            return false;
        }
        TenantResolver.setTenantedResource(new TenantResolver.TenantedResource(assignedDatasource));
        debug("[dropSchemaConstraints] org {} set tenanted resource to {}", organizationId, TenantResolver.getTenantedResource());

        queryExecutor.runQueriesInTransaction(String.format("call remove_all_constraints_in_schema('%s');", schemaName));
        debug("[dropSchemaConstraints] org {} dropped schema {} constraints", organizationId, schemaName);

        return true;
    }

    protected  <T> List<Future<T>> executeTasks(long timeoutInMilliseconds, boolean wait, List<Callable<T>> tasks) throws InterruptedException {
        debug("[executeTasks]");
        if (tasks == null || tasks.isEmpty()) {
            warn("[executeTasks] empty tasks list");
            return Collections.emptyList();
        }
        ExecutorService tp = Executors.newFixedThreadPool(Math.min(tasks.size(), MAX_THREADS ));
        List<Future<T>> result = tp.invokeAll(tasks, timeoutInMilliseconds, TimeUnit.MILLISECONDS);
        tp.shutdown();
        if (wait) {
            tp.awaitTermination(timeoutInMilliseconds, TimeUnit.MILLISECONDS);
        }
        return result;
    }

    private <T> List<Future<T>> runForAllTenants(long timeoutInMilliseconds, boolean transactional, boolean wait, Function<Long, T> f, BiFunction<QueryExecutor, Long, T> qef, BiFunction<EntityManager, Long, T> emf) {
        Stream<Long> ids = organizationRepository.findActiveOrganizationIdsAsList().stream();
        return runForTenants(timeoutInMilliseconds, transactional, wait, ids, f, qef, emf);
    }

    private <T> List<Future<T>> runForTenants(long timeoutInMilliseconds, boolean transactional, boolean wait, Stream<Long> orgIds, Function<Long, T> f, BiFunction<QueryExecutor, Long, T> qef, BiFunction<EntityManager, Long, T> emf) {
        try {
            List<Callable<T>> tasks = new ArrayList<>();
            if (orgIds != null) {
                orgIds.forEach((orgId) ->
                        tasks.add(() -> {
                            TenantResolver.setTenantedResource(new TenantResolver.TenantedResource(orgId));
                            if (f != null) {
                                return f.apply(orgId);
                            }
                            if (qef != null) {
                                return qef.apply(queryExecutor, orgId);
                            }
                            if (emf != null) {
                                return queryExecutor.runEntityManagerOperationForOrg(orgId, transactional, emf);
                            }
                            return null;
                        })
                );
            }
            return executeTasks(timeoutInMilliseconds, wait, tasks);
        } catch (Exception e) {
            error("[runForAllTenants]", e);
            throw new RuntimeException(e);
        }
    }
}
