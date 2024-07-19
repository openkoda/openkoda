package com.openkoda.service.dynamicentity;

import com.openkoda.core.form.FrontendMappingFieldDefinition;
import net.bytebuddy.description.type.TypeDescription;

import java.util.Collection;

import static com.openkoda.service.dynamicentity.DynamicEntityRegistrationService.PACKAGE;

public class DynamicEntityDescriptor {

    private String entityClassName;
    private String tableName;

    private String entityKey;
    private String repositoryName;
    private Collection<FrontendMappingFieldDefinition> fields;
    private Long timeMillis;
    private boolean isLoaded;

    private TypeDescription.Latent typeDescription;

    DynamicEntityDescriptor(String entityClassName, String tableName, String entityKey, String repositoryName,
                            Collection<FrontendMappingFieldDefinition> fields, Long timeMillis) {
        this.entityClassName = entityClassName;
        this.tableName = tableName;
        this.entityKey = entityKey;
        this.repositoryName = repositoryName;
        this.fields = fields;
        this.timeMillis = timeMillis;
        this.isLoaded = false;
        this.typeDescription = new DynamicEntityTypeDescription(PACKAGE + getSuffixedEntityClassName(), 0, null, null);
    }

    public String getSuffixedEntityClassName(){
        return entityClassName + getTimeSuffix();
    }
    public String getSuffixedRepositoryName(){
        return repositoryName + getTimeSuffix();
    }

    public boolean isLoadable(){
        return !isLoaded;
    }
    private String getTimeSuffix(){
        return timeMillis != null ? "_" + timeMillis : "";
    }
    public String getEntityClassName() {
        return entityClassName;
    }

    public void setEntityClassName(String entityClassName) {
        this.entityClassName = entityClassName;
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

    public Long getTimeMillis() {
        return timeMillis;
    }

    public void setTimeMillis(Long timeMillis) {
        this.timeMillis = timeMillis;
    }

    public String getEntityKey() {
        return entityKey;
    }

    public void setEntityKey(String entityKey) {
        this.entityKey = entityKey;
    }

    public TypeDescription.Latent getTypeDescription() {
        return typeDescription;
    }

    public void setTypeDescription(TypeDescription.Latent typeDescription) {
        this.typeDescription = typeDescription;
    }
}
