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

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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
    public Set<String> getColumns()
            throws SQLFaultException
    {
        try {
            ResultSetMetaData metaData = rs.getMetaData();
            Set<String> columns = new HashSet<>( metaData.getColumnCount() );
            for ( int i = 0; i < metaData.getColumnCount(); i++ ) {
                columns.add( metaData.getColumnName( i ) );
            }
            return Collections.unmodifiableSet( columns );

        } catch ( SQLException ex ) {
            throw new SQLFaultException( "Error getting columns names", ex );
        }
    }

    /**
     * Get an optional String from this record
     *
     * @param column The column containg the String
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
     * @param column The column containg the Enum
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
     * @param column The column containg the Blob
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
     * @param column The column containg the Clob
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
     * @param column The column containg the BigDecimal
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
     * @param column The column containg the byte array
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
     * @param column The column containg the Integer
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
     * @param column The column containg the Float
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
     * @param column The column containg the Long
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
     * @param column The column containg the Short
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
     * @param column The column containg the LocalDate
     * @return The optional LocalDate value
     * @throws SQLFaultException If the column is unknown or if the value cannot be returned
     */
    public Optional<LocalDate> getLocalDate( String column )
            throws SQLFaultException
    {
        try {
            Date date = rs.getDate( column );
            if ( date == null ) {
                return Optional.empty();
            }
            return Optional.of( date.toLocalDate() );

        } catch ( SQLException ex ) {
            throw new SQLFaultException( "Error getting SQL value", ex );
        }
    }

    /**
     * Get an optional {@link Instant} from this record
     *
     * @param column The column containg the Instant
     * @return The optional Instant value
     * @throws SQLFaultException If the column is unknown or if the value cannot be returned
     */
    public Optional<Instant> getInstant( String column )
            throws SQLFaultException
    {
        try {
            Timestamp ts = rs.getTimestamp( column );
            if ( ts == null ) {
                return Optional.empty();
            }
            return Optional.of( ts.toInstant() );

        } catch ( SQLException ex ) {
            throw new SQLFaultException( "Error getting SQL value", ex );
        }
    }

    /**
     * Get an optional {@link LocalTime} from this record
     *
     * @param column The column containg the LocalTime
     * @return The optional LocalTime value
     * @throws SQLFaultException If the column is unknown or if the value cannot be returned
     */
    public Optional<LocalTime> getLocalTime( String column )
            throws SQLFaultException
    {
        try {
            Time time = rs.getTime( column );
            if ( time == null ) {
                return Optional.empty();
            }
            return Optional.of( time.toLocalTime() );

        } catch ( SQLException ex ) {
            throw new SQLFaultException( "Error getting SQL value", ex );
        }
    }

    /**
     * Get an optional Boolean from this record
     *
     * @param column The column containg the Boolean
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

}
