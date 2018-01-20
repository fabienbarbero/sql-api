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
package org.kalgan.sql.helper;

import org.kalgan.sql.HasSQLConnection;
import org.kalgan.sql.SQLFaultException;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Fabien Barbero
 */
public class SQLHelper
{

    private final Connection conn;

    public SQLHelper( Connection conn )
    {
        this.conn = conn;
    }

    public SQLHelper( HasSQLConnection conn )
    {
        this.conn = conn.getConnection();
    }

    /**
     * Indicates if a table exists
     *
     * @param tableName The table name to search
     * @return true if the table has been found
     * @throws SQLFaultException Error getting the information
     */
    public boolean isTableExists( String tableName )
            throws SQLFaultException
    {
        try {
            DatabaseMetaData metaData = conn.getMetaData();
            try ( ResultSet rs = metaData.getTables( null, null, tableName, null ) ) {
                return rs.next();
            }

        } catch ( SQLException ex ) {
            throw new SQLFaultException( "Error getting SQL tables", ex );
        }
    }

    /**
     * Indicates if a table column exists
     *
     * @param tableName  The table name containing the column
     * @param columnName The column name to search
     * @return true if the column exists
     * @throws SQLFaultException Error getting the information
     */
    public boolean isTableColumnExists( String tableName, String columnName )
            throws SQLFaultException
    {
        try {
            DatabaseMetaData metaData = conn.getMetaData();
            try ( ResultSet rs = metaData.getColumns( null, null, tableName, columnName ) ) {
                return rs.next();
            }

        } catch ( SQLException ex ) {
            throw new SQLFaultException( "Error getting SQL columns", ex );
        }
    }

    /**
     * Get the tables information
     *
     * @return The information
     * @throws SQLFaultException Error getting the information
     */
    public List<SQLTable> getTables()
            throws SQLFaultException
    {
        try {
            DatabaseMetaData metaData = conn.getMetaData();
            try ( ResultSet rs = metaData.getTables( null, null, "%", null ) ) {
                List<SQLTable> tables = new ArrayList<>();
                while ( rs.next() ) {
                    String tableName = rs.getString( "TABLE_NAME" );
                    tables.add( new SQLTable( tableName, getColumns( tableName, metaData ) ) );
                }
                return tables;
            }

        } catch ( SQLException ex ) {
            throw new SQLFaultException( "Error getting SQL tables", ex );
        }
    }

    public SQLTable getTable( String tableName )
            throws SQLFaultException
    {
        try {
            DatabaseMetaData metaData = conn.getMetaData();
            try ( ResultSet rs = metaData.getTables( null, null, tableName, null ) ) {
                if ( rs.next() ) {
                    return new SQLTable( tableName, getColumns( tableName, metaData ) );
                }
                return null;
            }

        } catch ( SQLException ex ) {
            throw new SQLFaultException( "Error getting SQL tables", ex );
        }
    }

    private List<SQLColumn> getColumns( String tableName, DatabaseMetaData metaData )
            throws SQLException
    {
        List<String> primaryColumns = getPrimaryKeys( tableName, metaData );
        List<SQLIndex> indexes = getIndexes( tableName, metaData );
        List<SQLForeignKey> foreignKeys = getForeignKeys( tableName, metaData );

        try ( ResultSet rs = metaData.getColumns( null, null, tableName, null ) ) {
            List<SQLColumn> columns = new ArrayList<>();
            while ( rs.next() ) {
                String colName = rs.getString( "COLUMN_NAME" );
                List<SQLIndex> colIndexes = indexes.stream()
                        .filter( idx -> idx.getColumnName().equalsIgnoreCase( colName ) )
                        .collect( Collectors.toList() );
                List<SQLForeignKey> colForeignKeys = foreignKeys.stream()
                        .filter( key -> key.getFKColumnName().equalsIgnoreCase( colName ) )
                        .collect( Collectors.toList() );

                columns.add( new SQLColumn( rs, primaryColumns.contains( colName ),
                                            tableName, colIndexes, colForeignKeys ) );
            }
            return columns;
        }
    }

    private List<SQLForeignKey> getForeignKeys( String tableName, DatabaseMetaData metaData )
            throws SQLException
    {
        try ( ResultSet rs = metaData.getImportedKeys( null, null, tableName ) ) {
            List<SQLForeignKey> keys = new ArrayList<>();
            while ( rs.next() ) {
                keys.add( new SQLForeignKey( rs,
                                             SQLForeignKey.Rule.from( rs.getShort( "UPDATE_RULE" ) ),
                                             SQLForeignKey.Rule.from( rs.getShort( "DELETE_RULE" ) )
                ) );
            }
            return keys;
        }
    }

    private List<String> getPrimaryKeys( String tableName, DatabaseMetaData metaData )
            throws SQLException
    {
        try ( ResultSet rs = metaData.getImportedKeys( null, null, tableName ) ) {
            List<String> keys = new ArrayList<>();
            while ( rs.next() ) {
                keys.add( rs.getString( "PKCOLUMN_NAME" ) );
            }
            return keys;
        }
    }

    private List<SQLIndex> getIndexes( String tableName, DatabaseMetaData metaData )
            throws SQLException
    {
        try ( ResultSet rs = metaData.getIndexInfo( null, null, tableName, false, false ) ) {
            List<SQLIndex> indexes = new ArrayList<>();
            while ( rs.next() ) {
                indexes.add( new SQLIndex( rs ) );
            }
            return indexes;
        }
    }

}
