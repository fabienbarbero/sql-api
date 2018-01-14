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
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author Fabien Barbero
 */
public class SQLRunner
{

    private final Connection conn;

    public SQLRunner( Connection conn )
    {
        this.conn = conn;
    }

    public SQLRunner( HasSQLConnection tx )
    {
        this.conn = tx.getConnection();
    }

    /**
     * Select entities from a given SQL query
     *
     * @param <T>    The entity type to return
     * @param mapper The mapper used to build Java entities
     * @param query  The query to select entities
     * @return The entities found
     * @throws SQLFaultException Query error
     */
    public <T> List<T> query( SQLRecordMapper<T> mapper, SQLQueryBuilder query )
            throws SQLFaultException
    {
        try ( PreparedStatement st = prepareStatement( query, false ) ) {
            try ( ResultSet rs = st.executeQuery() ) {
                List<T> list = new ArrayList<>();
                while ( rs.next() ) {
                    list.add( mapper.buildEntity( new SQLRecord( rs ) ) );
                }
                return list;
            }

        } catch ( SQLException ex ) {
            throw new SQLFaultException( "Error executing SQL query", ex );
        }
    }

    /**
     * Select a single entity from a given SQL query
     *
     * @param <T>    The entity type to return
     * @param mapper The mapper used to build Java entity
     * @param query  The query to select the entity
     * @return The optional entity found
     * @throws SQLFaultException Query error
     */
    public <T> T querySingle( SQLRecordMapper<T> mapper, SQLQueryBuilder query )
            throws SQLFaultException
    {
        try ( PreparedStatement st = prepareStatement( query, false ) ) {
            try ( ResultSet rs = st.executeQuery() ) {
                if ( rs.next() ) {
                    return mapper.buildEntity( new SQLRecord( rs ) );
                }
                return null;
            }

        } catch ( SQLException ex ) {
            throw new SQLFaultException( "Error executing SQL query", ex );
        }
    }

    /**
     * Select entities from a given SQL query. The results are returned via an iterator to limit memory usage. It is
     * useful when getting large entities count.
     *
     * @param <T>       The entities type
     * @param mapper    The mapper used to build Java entities
     * @param fetchSize The fetch size. Limits the memory usage. The value depends on the SQL driver. If null, the whole
     *                  entities will be stored in the memory.
     * @param updatable true to indicate if the iterator can change entities during iteration (this consumes more memory)
     * @param query     The query to select the entities
     * @return The iterator handling the entities. Do not forget to close the iterator after the process.
     */
    public <T> SQLIterator<T> queryIterator( SQLRecordMapper<T> mapper,
                                             Integer fetchSize,
                                             boolean updatable,
                                             SQLQueryBuilder query )
    {
        try {
            PreparedStatement st = prepareStatement( query, updatable );
            if ( fetchSize != null ) {
                st.setFetchSize( fetchSize );
            }
            ResultSet rs = st.executeQuery();
            return new SQLIterator<>( rs, st, mapper );

        } catch ( SQLException ex ) {
            throw new SQLFaultException( "Error executing SQL query", ex );
        }
    }

    /**
     * Select entities from a given SQL query. The results are returned via an iterator to limit memory usage. It is
     * useful when getting large entities count.
     *
     * @param <T>       The entities type
     * @param mapper    The mapper used to build Java entities
     * @param fetchSize The fetch size. Limits the memory usage. The value depends on the SQL driver. If null, the whole
     *                  entities will be stored in the memory.
     * @param query     The query to select the entities
     * @return The iterator handling the entities. Do not forget to close the iterator after the process.
     */
    public <T> Stream<T> queryAsStream( SQLRecordMapper<T> mapper, Integer fetchSize, SQLQueryBuilder query )
    {
        SQLIterator<T> iterator = queryIterator( mapper, fetchSize, false, query );
        Spliterator<T> spliterator = Spliterators.spliteratorUnknownSize( iterator, Spliterator.ORDERED | Spliterator.IMMUTABLE );
        return StreamSupport.stream( spliterator, false ).onClose( iterator::close );
    }

    /**
     * Execute a "count" query
     *
     * @param query The query to count entities. It must starts with "select count(...)".
     * @return The counted entities
     * @throws SQLFaultException Query error
     */
    public long count( SQLQueryBuilder query )
            throws SQLFaultException
    {
        try ( PreparedStatement st = prepareStatement( query, false ) ) {
            try ( ResultSet rs = st.executeQuery() ) {
                if ( rs.next() ) {
                    return rs.getLong( 1 );
                }
                return 0;
            }

        } catch ( SQLException ex ) {
            throw new SQLFaultException( "Error executing SQL query", ex );
        }
    }

    /**
     * Execute a query for UPDATE, INSERT or DELETE
     *
     * @param query The query to execute
     * @return The modified record count
     * @throws SQLFaultException Query error
     */
    public int execute( SQLQueryBuilder query )
            throws SQLFaultException
    {
        try ( PreparedStatement st = prepareStatement( query, false ) ) {
            return st.executeUpdate();

        } catch ( SQLException ex ) {
            throw new SQLFaultException( "Error executing SQL query", ex );
        }
    }

    private PreparedStatement prepareStatement( SQLQueryBuilder query, boolean updatable )
            throws SQLException
    {
        PreparedStatement st = conn.prepareStatement( query.query.toString(),
                                                      ResultSet.TYPE_FORWARD_ONLY,
                                                      updatable ? ResultSet.CONCUR_UPDATABLE : ResultSet.CONCUR_READ_ONLY );
        int index = 1;
        for ( Object obj : query.params ) {
            if ( obj instanceof LocalDate ) {
                st.setDate( index, Date.valueOf( ( LocalDate ) obj ) );
            } else if ( obj instanceof Instant ) {
                st.setTimestamp( index, Timestamp.from( ( Instant ) obj ) );
            } else if ( obj instanceof LocalTime ) {
                st.setTime( index, Time.valueOf( ( LocalTime ) obj ) );
            } else if ( obj instanceof SQLObject ) {
                st.setObject( index, ( ( SQLObject ) obj ).toSQLObject( conn ) );
            } else {
                st.setObject( index, obj );
            }
            index++;
        }
        return st;
    }

}
