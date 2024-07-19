package com.openkoda.service.dynamicentity;

import com.openkoda.core.form.FrontendMappingFieldDefinition;

import java.util.*;
import java.util.stream.Collectors;

import static com.openkoda.core.helper.NameHelper.*;

public class DynamicEntityDescriptorFactory {

    private static final Map<String /* formName */, DynamicEntityDescriptor> map = new HashMap<>();

    public static List<DynamicEntityDescriptor> loadableInstances(){
        return map.values().stream().filter(DynamicEntityDescriptor::isLoadable).collect(Collectors.toList());
    }

    public static List<DynamicEntityDescriptor> instances(){
        return new ArrayList<>(map.values());
    }

    public static void create(String formName, String tableName, Collection<FrontendMappingFieldDefinition> fields, Long timeMillis){
        DynamicEntityDescriptor ded = new DynamicEntityDescriptor(toEntityClassName(formName), tableName, toEntityKey(formName), toRepositoryName(formName), fields, timeMillis);
        map.put(ded.getEntityKey(), ded);
    }


    public static DynamicEntityDescriptor getInstanceByEntityKey(String entityKey) {
        return map.get(entityKey);
    }
}
