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

package com.openkoda.service.upgrade;

import com.openkoda.service.user.RoleService;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.*;

import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * JUnits for {@link RoleService}
 *
 * @author Martyna Litkowska (mlitkowska@stratoflow.com)
 * @since 2019-01-28
 */
//@EnableMethodSecurity
public class DbVersionServiceTest {

    private DbVersionService service;

    @Test
    public void tryToUpgrade() throws SQLException, IOException {
        service = new DbVersionService(null, null, null, true);
        Connection con = mock(Connection.class);
        Statement stmt = mock(Statement.class);
        PreparedStatement prepStmt = mock(PreparedStatement.class);
        ResultSet resultSet = mock(ResultSet.class);
        when(stmt.executeQuery(DbVersionService.FIND_CURRENT)).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("major")).thenReturn(1);
        when(resultSet.getInt("minor")).thenReturn(0);
        when(resultSet.getInt("build")).thenReturn(0);
        when(resultSet.getInt("revision")).thenReturn(0);
        
        ResultSet resultSetInstalled = mock(ResultSet.class);
        ResultSet resultSetInstalled2 = mock(ResultSet.class);
        when(stmt.executeQuery(service.getFindInstalledQuery(service.getAppVersion()))).thenReturn(resultSetInstalled, resultSetInstalled2);
        when(resultSetInstalled.next()).thenReturn(false);
        
        when(resultSetInstalled2.getInt("major")).thenReturn(1,1,1,1);
        when(resultSetInstalled2.getInt("minor")).thenReturn(6,6,6,6);
        when(resultSetInstalled2.getInt("build")).thenReturn(0,1,2,2);
        when(resultSetInstalled2.getInt("revision")).thenReturn(0,0,0,1);
        when(resultSetInstalled2.next()).thenReturn(true, true, true, true, false);
        
        //when(resultSet.getBoolean("done")).thenReturn(false);
        when(con.createStatement()).thenReturn(stmt);
        when(stmt.execute(eq("SELECT * FROM TEST;"))).thenReturn(true);
        when(stmt.execute(eq("SELECT * FROM TEST2;"))).thenReturn(true);
        when(stmt.execute(eq("SELECT * FROM TEST3;"))).thenReturn(true);
        when(stmt.execute(eq("SELECT * FROM TEST_FAIL;"))).thenThrow(new SQLException("Table does not exist"));
        
        when(con.prepareStatement(DbVersionService.VERSION_INSERT)).thenReturn(prepStmt);
        service.tryUpgade(con);
        
        verify(con, times(6)).createStatement();
        verify(con, times(1)).rollback();
        verify(stmt, times(4)).execute(anyString());
        verify(prepStmt, times(4)).execute();
        verify(con, times(4)).commit();
        when(stmt.executeQuery(DbVersionService.FIND_CURRENT)).thenReturn(resultSet);
        when(resultSet.getInt("major")).thenReturn(1);
        when(resultSet.getInt("minor")).thenReturn(6);
        when(resultSet.getInt("build")).thenReturn(2);
        when(resultSet.getInt("revision")).thenReturn(2);
        
        service.tryUpgade(con);
        verify(con, times(9)).createStatement();
        verify(con, times(1)).rollback();
        verify(stmt, times(5)).execute(anyString());
        verify(prepStmt, times(5)).execute();
        verify(con, times(5)).commit();

    }


}
