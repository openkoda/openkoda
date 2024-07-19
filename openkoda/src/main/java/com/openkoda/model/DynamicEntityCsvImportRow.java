package com.openkoda.model;

import com.openkoda.model.common.OpenkodaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Entity
public class DynamicEntityCsvImportRow extends OpenkodaEntity {

    /**
     * Id of upload. Each line from one import should have same uploadId
     */
    @Column
    private Long uploadId;

    @Column
    private long lineNumber;

    /**
     * Determines if line is valid
     */
    @Column
    private Boolean valid;

    @Column
    private String entityKey;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> content = Map.of();

    public DynamicEntityCsvImportRow() {
        super(null);
    }

    public DynamicEntityCsvImportRow(Long organizationId) {
        super(organizationId);
    }

    public Long getUploadId() {
        return uploadId;
    }

    public void setUploadId(Long uploadId) {
        this.uploadId = uploadId;
    }

    public long getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(long lineNumber) {
        this.lineNumber = lineNumber;
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public String getEntityKey() {
        return entityKey;
    }

    public void setEntityKey(String entityKey) {
        this.entityKey = entityKey;
    }

    public Map<String, Object> getContent() {
        return content;
    }

    public void setContent(Map<String, Object> content) {
        this.content = content;
    }
}
