package com.openkoda.service.dynamicentity;

import com.openkoda.controller.ComponentProvider;
import com.openkoda.core.multitenancy.MultitenancyService;
import com.openkoda.model.DynamicEntity;
import jakarta.inject.Inject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Service
public class DynamicEntityService extends ComponentProvider {

    @Inject
    MultitenancyService multitenancyService;

    @Transactional(propagation = REQUIRES_NEW)
    public boolean createDynamicTableIfNotExists(String tableName) {
        if (!repositories.unsecure.nativeQueries.ifTableExists(tableName)) {
            repositories.unsecure.nativeQueries.createTable(tableName);
            repositories.unsecure.dynamicEntity.save(create(tableName));
        }
        if (MultitenancyService.isMultitenancy()) {
            String tableExistsSql = repositories.unsecure.nativeQueries.tableExistsSql();
            String tableSql = repositories.unsecure.nativeQueries.createTableSql(tableName);

            multitenancyService.runEntityManagerForAllTenantsInTransaction(1000, (em, orgId) -> {
                Boolean exists = (Boolean) em.createNativeQuery(tableExistsSql, Boolean.class).setParameter("tableName", tableName).getSingleResult();
                if (exists == null || !exists) {
                    em.createNativeQuery(tableSql).executeUpdate();
                    return true;
                }
                return false;
            });
//            notice table added
            multitenancyService.addTenantedTables(Collections.singletonList(tableName));
        }
        return true;
    }
    
    public Map<Object, String> getAll() {
        Map<Object, String> eventsClasses = new LinkedHashMap<>();
        eventsClasses.put(String.class.getName(), String.format("Plain String (%s)", String.class.getName()));
        services.dynamicEntityRegistration.dynamicEntityClasses.entrySet() .stream().forEach( de -> {
            eventsClasses.put(de.getValue().getName(), String.format(" %s (%s)", de.getKey(), de.getValue().getName()));
        });
        
        return eventsClasses;
    }

    private DynamicEntity create(String tableName) {
        DynamicEntity dynamicEntity = new DynamicEntity();
        dynamicEntity.setTableName(tableName);
        return dynamicEntity;
    }
}
