package com.openkoda.service.dynamicentity;

import com.openkoda.core.form.FrontendMappingFieldDefinition;
import com.openkoda.model.common.OpenkodaEntity;
import com.openkoda.repository.SecureRepository;
import org.apache.commons.lang.StringUtils;

import java.util.Collection;

public class DynamicEntityDescriptor {
    private String entityName;
    private String tableName;

    private String entityKey;
    private String repositoryName;
    private Collection<FrontendMappingFieldDefinition> fields;
    private Long timeMilis;
    private boolean isLoaded;

    DynamicEntityDescriptor(String entityName, String tableName, String entityKey, String repositoryName, Collection<FrontendMappingFieldDefinition> fields, Long timeMilis) {
        this.entityName = entityName;
        this.tableName = tableName;
        this.entityKey = entityKey;
        this.repositoryName = repositoryName;
        this.fields = fields;
        this.timeMilis = timeMilis;
        this.isLoaded = false;
    }

    public String getSufixedEntityName(){
        return entityName + getNameSufix();
    }
    public String getSufixedRepositoryName(){
        return repositoryName + getNameSufix();
    }

    public boolean isLoadable(){
        return !isLoaded;
    }
    private String getNameSufix(){
        return timeMilis != null ? "_" + timeMilis : "";
    }
    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public Collection<FrontendMappingFieldDefinition> getFields() {
        return fields;
    }

    public void setFields(Collection<FrontendMappingFieldDefinition> fields) {
        this.fields = fields;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public void setLoaded(boolean loaded) {
        isLoaded = loaded;
    }

    public Long getTimeMilis() {
        return timeMilis;
    }

    public void setTimeMilis(Long timeMilis) {
        this.timeMilis = timeMilis;
    }

    public String getEntityKey() {
        return entityKey;
    }

    public void setEntityKey(String entityKey) {
        this.entityKey = entityKey;
    }
}
