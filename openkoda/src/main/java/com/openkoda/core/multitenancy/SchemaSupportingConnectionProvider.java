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

import com.openkoda.core.helper.ReadableCode;
import org.hibernate.engine.jdbc.connections.internal.DatasourceConnectionProviderImpl;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Connection provider useful for simplified single database, multiple schema multitenancy deployment.
 * Will work also in single database, single schema scenario
 */
public class SchemaSupportingConnectionProvider extends DatasourceConnectionProviderImpl implements ReadableCode {

    @Override
    public Connection getConnection() throws SQLException {
        TenantResolver.TenantedResource tr = TenantResolver.getTenantedResource();
        Connection c = super.getConnection();
        String setSearchPathStatement = String.format("set search_path to org_%d,public", tr.organizationId);
        c.prepareStatement(setSearchPathStatement).execute();
        return c;
    }

    @Override
    public void closeConnection(Connection conn) throws SQLException {
        conn.prepareStatement("set search_path to public").execute();
        super.closeConnection(conn);
    }

}