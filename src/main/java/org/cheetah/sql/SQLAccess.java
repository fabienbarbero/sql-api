/*
 * Copyright (C) 2016 fabien.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package org.cheetah.sql;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Fabien Barbero
 */
public class SQLAccess
        implements AutoCloseable, HasSQLConnection
{

    public static SQLAccess of( DataSource ds )
    {
        return of( ds, false );
    }

    public static SQLAccess of( DataSource ds, boolean readOnly )
    {
        try {
            Connection conn = ds.getConnection();
            conn.setAutoCommit( false );
            conn.setReadOnly( readOnly );
            return new SQLAccess( conn );

        } catch ( SQLException ex ) {
            throw new SQLFaultException( "Error getting sql access", ex );
        }
    }

    public static SQLAccess of( Connection conn )
    {
        return of( conn, false );
    }

    public static SQLAccess of( Connection conn, boolean readOnly )
    {
        try {
            conn.setAutoCommit( false );
            conn.setReadOnly( readOnly );
            return new SQLAccess( conn );

        } catch ( SQLException ex ) {
            throw new SQLFaultException( "Error getting sql access", ex );
        }
    }

    private final Connection conn;

    private SQLAccess( Connection conn )
    {
        this.conn = conn;
    }

    @Override
    public Connection getConnection()
    {
        return conn;
    }

    @Override
    public void close()
            throws Exception
    {

    }
}
