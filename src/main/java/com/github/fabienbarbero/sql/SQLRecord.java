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
package com.github.fabienbarbero.sql;

import java.math.BigDecimal;
import java.sql.*;
import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

/**
 * Represents a SQL record (row). It uses {@link Optional} objects for columns values.
 *
 * @author Fabien Barbero
 */
public class SQLRecord
{

    private final ResultSet rs;

    SQLRecord( ResultSet rs )
    {
        this.rs = rs;
    }

    /**
     * Get the available columns from this record
     *
     * @return The columns
     * @throws SQLFaultException If the columns names cannot be returned
     */
    public List<Column> getColumns()
            throws SQLFaultException
    {
        try {
            ResultSetMetaData metaData = rs.getMetaData();
            List<Column> columns = new ArrayList<>( metaData.getColumnCount() );
            for ( int i = 0; i < metaData.getColumnCount(); i++ ) {
                columns.add( new Column( metaData.getColumnName( i + 1 ),
                                         metaData.getTableName( i + 1 ) ) );
            }
            return Collections.unmodifiableList( columns );

        } catch ( SQLException ex ) {
            throw new SQLFaultException( "Error getting columns names", ex );
        }
    }

    /**
     * Get an optional String from this record
     *
     * @param column The column containing the String
     * @return The optional String value
     * @throws SQLFaultException If the column is unknown or if the value cannot be returned
     */
    public Optional<String> getString( String column )
            throws SQLFaultException
    {
        try {
            return Optional.ofNullable( rs.getString( column ) );
        } catch ( SQLException ex ) {
            throw new SQLFaultException( "Error getting SQL value", ex );
        }
    }

    /**
     * Get an optional Enum from this record. The enums MUST be stored as String in the database.
     *
     * @param column The column containing the Enum
     * @param type   The enum type
     * @param <E>    The enum type
     * @return The optional Enum value
     * @throws SQLFaultException If the column is unknown or if the value cannot be returned
     */
    public <E extends Enum<E>> Optional<E> getEnum( String column, Class<E> type )
            throws SQLFaultException
    {
        return getString( column ).map( str -> Enum.valueOf( type, str ) );
    }

    /**
     * Get an optional {@link Blob} from this record
     *
     * @param column The column containing the Blob
     * @return The optional Blob value
     * @throws SQLFaultException If the column is unknown or if the value cannot be returned
     */
    public Optional<Blob> getBlob( String column )
            throws SQLFaultException
    {
        try {
            return Optional.ofNullable( rs.getBlob( column ) );
        } catch ( SQLException ex ) {
            throw new SQLFaultException( "Error getting SQL value", ex );
        }
    }

    /**
     * Get an optional {@link Clob} from this record
     *
     * @param column The column containing the Clob
     * @return The optional Clob value
     * @throws SQLFaultException If the column is unknown or if the value cannot be returned
     */
    public Optional<Clob> getClob( String column )
            throws SQLFaultException
    {
        try {
            return Optional.ofNullable( rs.getClob( column ) );
        } catch ( SQLException ex ) {
            throw new SQLFaultException( "Error getting SQL value", ex );
        }
    }

    /**
     * Get an optional {@link BigDecimal} from this record
     *
     * @param column The column containing the BigDecimal
     * @return The optional BigDecimal value
     * @throws SQLFaultException If the column is unknown or if the value cannot be returned
     */
    public Optional<BigDecimal> getBigDecimal( String column )
            throws SQLFaultException
    {
        try {
            return Optional.ofNullable( rs.getBigDecimal( column ) );
        } catch ( SQLException ex ) {
            throw new SQLFaultException( "Error getting SQL value", ex );
        }
    }

    /**
     * Get an optional byte array from this record
     *
     * @param column The column containing the byte array
     * @return The optional byte array value
     * @throws SQLFaultException If the column is unknown or if the value cannot be returned
     */
    public Optional<byte[]> getBytes( String column )
            throws SQLFaultException
    {
        try {
            return Optional.ofNullable( rs.getBytes( column ) );
        } catch ( SQLException ex ) {
            throw new SQLFaultException( "Error getting SQL value", ex );
        }
    }

    /**
     * Get an optional Integer from this record
     *
     * @param column The column containing the Integer
     * @return The optional Integer value
     * @throws SQLFaultException If the column is unknown or if the value cannot be returned
     */
    public Optional<Integer> getInteger( String column )
            throws SQLFaultException
    {
        try {
            int value = rs.getInt( column );
            if ( rs.wasNull() ) {
                return Optional.empty();
            }
            return Optional.of( value );

        } catch ( SQLException ex ) {
            throw new SQLFaultException( "Error getting SQL value", ex );
        }
    }

    /**
     * Get an optional Float from this record
     *
     * @param column The column containing the Float
     * @return The optional Float value
     * @throws SQLFaultException If the column is unknown or if the value cannot be returned
     */
    public Optional<Float> getFloat( String column )
            throws SQLFaultException
    {
        try {
            float value = rs.getFloat( column );
            if ( rs.wasNull() ) {
                return Optional.empty();
            }
            return Optional.of( value );

        } catch ( SQLException ex ) {
            throw new SQLFaultException( "Error getting SQL value", ex );
        }
    }

    /**
     * Get an optional Long from this record
     *
     * @param column The column containing the Long
     * @return The optional Long value
     * @throws SQLFaultException If the column is unknown or if the value cannot be returned
     */
    public Optional<Long> getLong( String column )
            throws SQLFaultException
    {
        try {
            long value = rs.getLong( column );
            if ( rs.wasNull() ) {
                return Optional.empty();
            }
            return Optional.of( value );

        } catch ( SQLException ex ) {
            throw new SQLFaultException( "Error getting SQL value", ex );
        }
    }

    /**
     * Get an optional Short from this record
     *
     * @param column The column containing the Short
     * @return The optional Short value
     * @throws SQLFaultException If the column is unknown or if the value cannot be returned
     */
    public Optional<Short> getShort( String column )
            throws SQLFaultException
    {
        try {
            short value = rs.getShort( column );
            if ( rs.wasNull() ) {
                return Optional.empty();
            }
            return Optional.of( value );

        } catch ( SQLException ex ) {
            throw new SQLFaultException( "Error getting SQL value", ex );
        }
    }

    /**
     * Get an optional {@link LocalDate} from this record
     *
     * @param column The column containing the LocalDate
     * @return The optional LocalDate value
     * @throws SQLFaultException If the column is unknown or if the value cannot be returned
     */
    public Optional<LocalDate> getLocalDate( String column )
            throws SQLFaultException
    {
        try {
            Date date = rs.getDate( column );
            return Optional.ofNullable( date ).map( Date::toLocalDate );

        } catch ( SQLException ex ) {
            throw new SQLFaultException( "Error getting SQL value", ex );
        }
    }

    /**
     * Get an optional {@link Instant} from this record
     *
     * @param column The column containing the Instant
     * @return The optional Instant value
     * @throws SQLFaultException If the column is unknown or if the value cannot be returned
     */
    public Optional<Instant> getInstant( String column )
            throws SQLFaultException
    {
        try {
            Timestamp ts = rs.getTimestamp( column );
            return Optional.ofNullable( ts ).map( Timestamp::toInstant );

        } catch ( SQLException ex ) {
            throw new SQLFaultException( "Error getting SQL value", ex );
        }
    }

    /**
     * Get an optional {@link LocalTime} from this record
     *
     * @param column The column containing the LocalTime
     * @return The optional LocalTime value
     * @throws SQLFaultException If the column is unknown or if the value cannot be returned
     */
    public Optional<LocalTime> getLocalTime( String column )
            throws SQLFaultException
    {
        try {
            Time time = rs.getTime( column );
            return Optional.ofNullable( time ).map( Time::toLocalTime );

        } catch ( SQLException ex ) {
            throw new SQLFaultException( "Error getting SQL value", ex );
        }
    }

    /**
     * Get an optional Boolean from this record
     *
     * @param column The column containing the Boolean
     * @return The optional Boolean value
     * @throws SQLFaultException If the column is unknown or if the value cannot be returned
     */
    public Optional<Boolean> getBoolean( String column )
            throws SQLFaultException
    {
        try {
            boolean value = rs.getBoolean( column );
            if ( rs.wasNull() ) {
                return Optional.empty();
            }
            return Optional.of( value );

        } catch ( SQLException ex ) {
            throw new SQLFaultException( "Error getting SQL value", ex );
        }
    }

    /**
     * Represents a column in a {@link SQLRecord}
     */
    public static class Column
    {
        private final String name;
        private final String table;

        private Column( String name, String table )
        {
            this.name = name;
            this.table = table;
        }

        /**
         * Get the column name
         *
         * @return The name
         */
        public String getName()
        {
            return name;
        }

        /**
         * Get the table name containing the column
         *
         * @return The table name
         */
        public String getTable()
        {
            return table;
        }

        @Override
        public boolean equals( Object obj )
        {
            if ( obj instanceof Column ) {
                Column col = ( Column ) obj;
                return table.equals( col.table ) && name.equals( col.name );
            }
            return false;
        }

        @Override
        public int hashCode()
        {
            return 253543 + Objects.hash( name, table );
        }

        @Override
        public String toString()
        {
            return table + "." + name;
        }
    }

}
