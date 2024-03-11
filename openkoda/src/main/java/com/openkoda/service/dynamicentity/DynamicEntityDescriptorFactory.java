package com.openkoda.service.dynamicentity;

import com.openkoda.core.form.FrontendMappingFieldDefinition;
import com.openkoda.core.helper.NameHelper;

import java.util.*;
import java.util.stream.Collectors;

import static com.openkoda.core.helper.NameHelper.*;

public class DynamicEntityDescriptorFactory {

    private static final Map<String, DynamicEntityDescriptor> map = new HashMap<>();
    public static List<DynamicEntityDescriptor> loadableInstances(){
        return map.values().stream().filter(DynamicEntityDescriptor::isLoadable).collect(Collectors.toList());
    }
    public static List<DynamicEntityDescriptor> instances(){
        return new ArrayList<>(map.values());
    }
    public static void create(String tableName, Collection<FrontendMappingFieldDefinition> fields, Long timeMilis){
        DynamicEntityDescriptor ded = new DynamicEntityDescriptor(toEntityName(tableName), tableName, toEntityKey(tableName), toRepositoryName(tableName), fields, timeMilis);
        map.put(tableName, ded);
    }
    public static DynamicEntityDescriptor getLoadableInstance(String key){
        DynamicEntityDescriptor ded = map.get(key);
        return ded != null && ded.isLoadable() ? ded : null;
    }
}
