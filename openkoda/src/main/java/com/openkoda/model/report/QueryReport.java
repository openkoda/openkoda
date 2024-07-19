package com.openkoda.model.report;

import com.openkoda.model.PrivilegeNames;
import com.openkoda.model.common.OpenkodaEntity;
import jakarta.persistence.Entity;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Formula;

@Entity
public class QueryReport extends OpenkodaEntity {

    private String name;
    private String query;

    @Formula("( '" + PrivilegeNames._readOrgData + "' )")
    private String requiredReadPrivilege;

    @Formula("( '" + PrivilegeNames._manageOrgData + "' )")
    private String requiredWritePrivilege;

    public QueryReport(Long organizationId) {
        super(organizationId);
    }

    public QueryReport() {
        super(null);
    }

    public QueryReport(String query) {
        super(null);
        this.query = query;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getFileName() {
        return StringUtils.isNotEmpty(name) ? name.replaceAll("\\s+", "_").toLowerCase() : "report";
    }
}
