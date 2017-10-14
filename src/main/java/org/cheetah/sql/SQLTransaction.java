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
import javax.sql.DataSource;

/**
 *
 * @author Fabien Barbero
 */
public class SQLTransaction
        implements AutoCloseable, HasSQLConnection
{

    /**
     * Begin a new SQL transaction in R/W mode and default isolation level
     *
     * @param ds The SQL data source to use
     * @return The new transaction
     */
    public static SQLTransaction begin( DataSource ds )
    {
        try {
            return begin( ds.getConnection(), false, null );
        } catch ( SQLException ex ) {
            throw new SQLFaultException( "Error begenning SQL transaction", ex );
        }
    }

    /**
     * Begin a new SQL transaction
     *
     * @param ds       The SQL data source to use
     * @param readOnly Indicates if the transaction must be read-only
     * @param level    The isolation level. If null the default value is used
     * @return The new transaction
     */
    public static SQLTransaction begin( DataSource ds, boolean readOnly, IsolationLevel level )
    {
        try {
            return begin( ds.getConnection(), readOnly, level );
        } catch ( SQLException ex ) {
            throw new SQLFaultException( "Error begenning SQL transaction", ex );
        }
    }

    /**
     * Begin a new SQL transaction in R/W mode and default isolation level
     *
     * @param conn The base connection to use
     * @return The new transaction
     */
    public static SQLTransaction begin( Connection conn )
    {
        return begin( conn, false, null );
    }

    /**
     * Begin a new SQL transaction
     *
     * @param conn     The base connection to use
     * @param readOnly Indicates if the transaction must be read-only
     * @param level    The isolation level. If null the default value is used
     * @return The new transaction
     */
    public static SQLTransaction begin( Connection conn, boolean readOnly, IsolationLevel level )
    {
        try {
            conn.setAutoCommit( false );
            conn.setReadOnly( readOnly );
            if ( level != null ) {
                conn.setTransactionIsolation( level.value );
            }
            return new SQLTransaction( conn );

        } catch ( SQLException ex ) {
            throw new SQLFaultException( "Error begenning SQL transaction", ex );
        }
    }

    private final Connection conn;

    private SQLTransaction( Connection conn )
    {
        this.conn = conn;
    }

    /**
     * Create a new savepoint
     *
     * @param name The savepoint unique name
     * @return The savepoint
     */
    public SQLSavepoint createSavepoint( String name )
    {
        try {
            return new SQLSavepoint( conn.setSavepoint( name ), conn );
        } catch ( SQLException ex ) {
            throw new SQLFaultException( "Error creating SQL savepoint", ex );
        }
    }

    /**
     * Commit the current transaction
     *
     * @throws SQLFaultException Commit error
     */
    public void commit()
            throws SQLFaultException
    {
        try {
            conn.commit();
        } catch ( SQLException ex ) {
            throw new SQLFaultException( "Error commiting SQL transaction", ex );
        }
    }

    /**
     * Rollback the current transaction
     *
     * @throws SQLFaultException Rollback error
     */
    public void rollback()
            throws SQLFaultException
    {
        try {
            conn.rollback();
        } catch ( SQLException ex ) {
            throw new SQLFaultException( "Error rollbacking transaction", ex );
        }
    }

    @Override
    public void close()
            throws SQLFaultException
    {
        try {
            if ( !conn.isClosed() ) {
                conn.close();
            }

        } catch ( SQLException ex ) {
            throw new SQLFaultException( "Error closing SQL transaction", ex );
        }
    }

    @Override
    public Connection getConnection()
    {
        return conn;
    }

    public enum IsolationLevel
    {

        /**
         * A constant indicating that dirty reads, non-repeatable reads and phantom reads can occur. This level allows a
         * row changed by one transaction to be read by another transaction before any changes in that row have been
         * committed (a "dirty read"). If any of the changes are rolled back, the second transaction will have retrieved
         * an invalid row.
         */
        READ_UNCOMMITTED( Connection.TRANSACTION_READ_UNCOMMITTED ),
        /**
         * A constant indicating that dirty reads, non-repeatable reads and phantom reads can occur. This level allows a
         * row changed by one transaction to be read by another transaction before any changes in that row have been
         * committed (a "dirty read"). If any of the changes are rolled back, the second transaction will have retrieved
         * an invalid row.
         */
        READ_COMMITTED( Connection.TRANSACTION_READ_COMMITTED ),
        /**
         * A constant indicating that dirty reads and non-repeatable reads are prevented; phantom reads can occur. This
         * level prohibits a transaction from reading a row with uncommitted changes in it, and it also prohibits the
         * situation where one transaction reads a row, a second transaction alters the row, and the first transaction
         * rereads the row, getting different values the second time (a "non-repeatable read").
         */
        REPEATABLE_READ( Connection.TRANSACTION_REPEATABLE_READ ),
        /**
         * A constant indicating that dirty reads, non-repeatable reads and phantom reads are prevented. This level
         * includes the prohibitions in TRANSACTION_REPEATABLE_READ and further prohibits the situation where one
         * transaction reads all rows that satisfy a WHERE condition, a second transaction inserts a row that satisfies
         * that WHERE condition, and the first transaction rereads for the same condition, retrieving the additional
         * "phantom" row in the second read.
         */
        SERIALIZABLE( Connection.TRANSACTION_SERIALIZABLE );

        private final int value;

        private IsolationLevel( int value )
        {
            this.value = value;
        }

    }

}
