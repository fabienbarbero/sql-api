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
package org.kalgan.sql;

import java.util.List;

/**
 *
 * @author Fabien Barbero
 */
class UserDAOImpl
        implements UserDAO, SQLRecordMapper<User>
{

    private final SQLRunner executor;

    public UserDAOImpl( SQLTransaction tx )
    {
        executor = new SQLRunner( tx );
    }

    @Override
    public User buildEntity( SQLRecord record )
    {
        User user = new User();
        user.setUuid( record.getString( "UUID" ).get() );
        user.setEmail( record.getString( "EMAIL" ).get() );
        user.setName( record.getString( "NAME" ).get() );
        return user;
    }

    @Override
    public void addEntity( User entity )
            throws SQLFaultException
    {
        executor.execute( new SQLQueryBuilder( "insert into USERS (UUID, EMAIL, NAME) values (?,?,?)",
                                       entity.getUuid(), entity.getEmail(), entity.getName() ) );
    }

    @Override
    public void updateEntity( User entity )
            throws SQLFaultException
    {
        executor.execute( new SQLQueryBuilder( "update USERS set EMAIL=?, NAME=? where UUID=?",
                                       entity.getEmail(), entity.getName(), entity.getUuid() ) );
    }

    @Override
    public User findByEmail( String email )
            throws SQLFaultException
    {
        return executor.querySingle( this, new SQLQueryBuilder( "select * from USERS where EMAIL=?", email ) );
    }

    @Override
    public List<User> findAll()
            throws SQLFaultException
    {
        return executor.query( this, new SQLQueryBuilder( "select * from USERS" ) );
    }

    @Override
    public User find( String key )
            throws SQLFaultException
    {
        return executor.querySingle( this, new SQLQueryBuilder( "select * from USERS where UUID=?", key ) );
    }

    @Override
    public void deleteEntity( String key )
            throws SQLFaultException
    {
        executor.execute( new SQLQueryBuilder( "delete from USERS where UUID=?", key ) );
    }

    @Override
    public void deleteEntity( User entity )
            throws SQLFaultException
    {
        executor.execute( new SQLQueryBuilder( "delete from USERS where UUID=?", entity.getUuid() ) );
    }

}
