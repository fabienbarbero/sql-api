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

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;

/**
 *
 * @author Fabien Barbero
 */
public class SQLSavepoint
        implements AutoCloseable
{

    private final Savepoint sp;
    private final Connection conn;

    SQLSavepoint( Savepoint sp, Connection conn )
    {
        this.sp = sp;
        this.conn = conn;
    }

    /**
     * Get the savepoint name
     *
     * @return The name
     */
    public String getName()
    {
        try {
            return sp.getSavepointName();
        } catch ( SQLException ex ) {
            throw new SQLFaultException( "Error getting savepoint name", ex );
        }
    }

    /**
     * Rollback the savepoint to its creation state
     *
     * @throws SQLFaultException Rollback error
     */
    public void rollback()
            throws SQLFaultException
    {
        try {
            conn.rollback( sp );
        } catch ( SQLException ex ) {
            throw new SQLFaultException( "Savepoint rollback error", ex );
        }
    }

    @Override
    public void close()
            throws SQLFaultException
    {
        try {
            conn.releaseSavepoint( sp );
        } catch ( SQLException ex ) {
            throw new SQLFaultException( "Savepoint release error", ex );
        }
    }

}
