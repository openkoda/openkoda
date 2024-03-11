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

import com.openkoda.core.tracker.LoggingComponentWithRequestId;
import com.openkoda.model.component.FrontendResource;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

/**
 * Tenant Resolver contract is to:
 * <ol>
 * <li> Store information about TenantedResource in the Thread Local</li>
 * <li> Provide the TenantedResource</li>
 * <li> Provide tenant identifier to Hibernate</li>
 * </ol>
 *
 * In Hibernate, tenant identifier is a simple string.
 * The Tenant Database Allocation Strategy needs more information (provided by TenantedResource)
 * to decide which connection should be selected.
 * The Tenanted resource is kept in ThreadLocal, and Hibernate stores the tenantIdentifier (which is a hashcode of the TenantedResource)
 */
@Component
public class TenantResolver implements CurrentTenantIdentifierResolver, LoggingComponentWithRequestId {

    public static final TenantedResource nonExistingTenantedResource =
            new TenantedResource(null, FrontendResource.AccessLevel.PUBLIC);

    /**
     * TenantedResource keeps attributes that should determine which datasource and db schema should be used to handle
     * resource request.
     *
     * For instance in a scenario where we have primary database handling read/writes operation
     * and secondary database handling read operations, we could use database allocation strategy to direct traffic based
     * on the HTTP method attribute.
     */
    public static class TenantedResource {

        /**
         * Organization id
         */
        public final Long organizationId;

        /**
         * Host
         */
        public final String host;

        /**
         * Entity key
         */
        public final String entityKey;

        /**
         * HTTP method (GET, POST, etc.)
         */
        public final String method;

        public final FrontendResource.AccessLevel accessLevel;

        /**
         * Datasource index, a hint Datasource allocation strategy that this datasource should be selected.
         */
        public final int preselectedDatasourceIndex;

        public TenantedResource(Long organizationId, String host, String entityKey, String method, FrontendResource.AccessLevel accessLevel) {
            this.organizationId = organizationId;
            this.host = host;
            this.entityKey = entityKey;
            this.method = method;
            this.preselectedDatasourceIndex = -1;
            this.accessLevel = accessLevel;
        }

        public TenantedResource(Long organizationId) {
            this.organizationId = organizationId;
            this.host = null;
            this.entityKey = null;
            this.method = null;
            this.preselectedDatasourceIndex = -1;
            this.accessLevel = FrontendResource.AccessLevel.PUBLIC;
        }

        public TenantedResource(Long organizationId, FrontendResource.AccessLevel accessLevel) {
            this.organizationId = organizationId;
            this.host = null;
            this.entityKey = null;
            this.method = null;
            this.preselectedDatasourceIndex = -1;
            this.accessLevel = accessLevel;
        }

        public TenantedResource(int preselectedDatasourceIndex) {
            this.organizationId = null;
            this.host = null;
            this.entityKey = null;
            this.method = null;
            this.preselectedDatasourceIndex = preselectedDatasourceIndex;
            this.accessLevel = FrontendResource.AccessLevel.PUBLIC;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TenantedResource that = (TenantedResource) o;

            if (organizationId != null ? !organizationId.equals(that.organizationId) : that.organizationId != null)
                return false;
            if (host != null ? !host.equals(that.host) : that.host != null) return false;
            if (entityKey != null ? !entityKey.equals(that.entityKey) : that.entityKey != null) return false;
            return method != null ? method.equals(that.method) : that.method == null;
        }

        @Override
        public int hashCode() {
            int result = organizationId != null ? organizationId.hashCode() : 0;
            result = 31 * result + (host != null ? host.hashCode() : 0);
            result = 31 * result + (entityKey != null ? entityKey.hashCode() : 0);
            result = 31 * result + (method != null ? method.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return
                    "org=" + organizationId +
                    ", host='" + host + '\'' +
                    ", entityKey='" + entityKey + '\'' +
                    ", method='" + method + '\'' +
                    ", pre=" + preselectedDatasourceIndex +
                    '}';
        }
    }

    /**
     * Keeps TenantedResource for the current thread
     */
    private static ThreadLocal<TenantedResource> tenantedResource = new ThreadLocal<>();

    public static boolean setTenantedResource(TenantedResource tr) {
        tenantedResource.set(tr);
        return true;
    }

    public static TenantedResource getTenantedResource() {
        return tenantedResource.get() == null ? nonExistingTenantedResource : tenantedResource.get();
    }

    @Override
    public String resolveCurrentTenantIdentifier() {
        return String.valueOf(getTenantedResource().hashCode());
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return false;
    }

}